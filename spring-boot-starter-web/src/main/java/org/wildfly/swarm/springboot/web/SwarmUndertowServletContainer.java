package org.wildfly.swarm.springboot.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

import javax.servlet.ServletException;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.MimeMapping;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.api.ServletStackTraces;
import io.undertow.servlet.handlers.DefaultServlet;
import org.jboss.msc.service.ServiceName;
import org.springframework.boot.context.embedded.AbstractConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.wildfly.swarm.springboot.util.Bridge;

/**
 * @author Bob McWhirter
 */
public class SwarmUndertowServletContainer implements EmbeddedServletContainer {

    private final SwarmUndertowServletContainerFactory factory;

    public SwarmUndertowServletContainer(SwarmUndertowServletContainerFactory factory, ServletContextInitializer... initializers) {
        this.factory = factory;
        this.factory.getJspServlet().setRegistered(false);

        System.err.println("CONTEXT --> " + this.factory.getContextPath());

        Object servletContainerService = Bridge.lookup(ServiceName.JBOSS.append("undertow", "servlet-container", "default"));

        try {
            Method getContainer = servletContainerService.getClass().getMethod("getServletContainer");
            ServletContainer servletContainer = (ServletContainer) getContainer.invoke(servletContainerService);

            DeploymentManager manager = createDeploymentManager(servletContainer, initializers);
            manager.deploy();
            HttpHandler httpHandler = manager.start();

            if (!this.factory.getContextPath().equals("")) {
                httpHandler = Handlers.path().addPrefixPath(this.factory.getContextPath(), httpHandler);
            }

            Object hostService = Bridge.lookup(ServiceName.JBOSS.append("undertow", "default-server", "default-host"));

            Method registerDeployment = hostService.getClass().getMethod("registerDeployment", Deployment.class, HttpHandler.class);

            registerDeployment.invoke(hostService, manager.getDeployment(), httpHandler);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    private DeploymentManager createDeploymentManager(
            ServletContainer container,
            ServletContextInitializer... initializers) {
        DeploymentInfo deployment = Servlets.deployment();

        Initializer initializer = new Initializer(initializers);
        ServletContainerInitializerInfo initInfo = new ServletContainerInitializerInfo(
                Initializer.class,
                initializer.instanceFactory(),
                Collections.emptySet()
        );
        deployment.addServletContainerInitalizers(initInfo);

        deployment.setClassLoader(Thread.currentThread().getContextClassLoader());
        deployment.setContextPath(this.factory.getContextPath());
        deployment.setDisplayName("spring-boot");
        deployment.setDeploymentName("spring-boot");
        if (this.factory.isRegisterDefaultServlet()) {
            deployment.addServlet(Servlets.servlet("default", DefaultServlet.class));
        }
        configureErrorPages(deployment);
        deployment.setServletStackTraces(ServletStackTraces.NONE);
        deployment.setResourceManager(this.factory.getDocumentRootResourceManager());
        configureMimeMappings(deployment);
        for (UndertowDeploymentInfoCustomizer customizer : this.factory.getDeploymentInfoCustomizers()) {
            customizer.customize(deployment);
        }
        /*
        if (isAccessLogEnabled()) {
            configureAccessLog(deployment);
        }
        if (isPersistSession()) {
            File dir = getValidSessionStoreDir();
            deployment.setSessionPersistenceManager(new FileSessionPersistence(dir));
        }
        */
        DeploymentManager manager = container.addDeployment(deployment);
        manager.deploy();
        SessionManager sessionManager = manager.getDeployment().getSessionManager();
        //int sessionTimeout = (getSessionTimeout() > 0 ? getSessionTimeout() : -1);
        sessionManager.setDefaultSessionTimeout(-1);
        return manager;
    }

    private void configureMimeMappings(DeploymentInfo servletBuilder) {
        for (MimeMappings.Mapping mimeMapping : this.factory.getMimeMappings()) {
            servletBuilder.addMimeMapping(new MimeMapping(mimeMapping.getExtension(),
                    mimeMapping.getMimeType()));
        }
    }

    private void configureErrorPages(DeploymentInfo servletBuilder) {
        for (ErrorPage errorPage : this.factory.getErrorPages()) {
            servletBuilder.addErrorPage(getUndertowErrorPage(errorPage));
        }
    }

    private io.undertow.servlet.api.ErrorPage getUndertowErrorPage(ErrorPage errorPage) {
        if (errorPage.getStatus() != null) {
            return new io.undertow.servlet.api.ErrorPage(errorPage.getPath(),
                    errorPage.getStatusCode());
        }
        if (errorPage.getException() != null) {
            return new io.undertow.servlet.api.ErrorPage(errorPage.getPath(),
                    errorPage.getException());
        }
        return new io.undertow.servlet.api.ErrorPage(errorPage.getPath());
    }

    @Override
    public void start() throws EmbeddedServletContainerException {

    }

    @Override
    public void stop() throws EmbeddedServletContainerException {

    }

    @Override
    public int getPort() {
        // TODO fixme
        return 8080;
    }
}
