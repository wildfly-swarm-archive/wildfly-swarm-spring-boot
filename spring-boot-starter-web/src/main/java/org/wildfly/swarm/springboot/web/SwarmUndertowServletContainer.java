package org.wildfly.swarm.springboot.web;

import java.util.concurrent.CountDownLatch;

import javax.servlet.ServletContainerInitializer;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.web.SpringServletContainerInitializer;
import org.wildfly.swarm.container.DeploymentException;
import org.wildfly.swarm.container.internal.Deployer;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.springboot.MSCBridgeActivator;
import org.wildfly.swarm.springboot.SpringApplication;
import org.wildfly.swarm.springboot.util.Bridge;
import org.wildfly.swarm.undertow.WARArchive;

/**
 * @author Bob McWhirter
 */
public class SwarmUndertowServletContainer implements EmbeddedServletContainer {

    private final Deployer deployer;

    private final ServletContextInitializer[] initializers;

    private final CountDownLatch latch = new CountDownLatch(1);

    public SwarmUndertowServletContainer(Deployer deployer, ServletContextInitializer... initializers) {

        this.deployer = deployer;
        this.initializers = initializers;

        for (ServletContextInitializer each : initializers) {
            int counter = 0;
            System.err.println( "EACH -- " + each + " // " + each.getClass() );
            Bridge.addObject(each, SwarmSpringWebApplicationInitializer.INIT.append("" + (++counter)));
        }

        WARArchive archive = ShrinkWrap.create(WARArchive.class, "spring-boot-app.war");
        try {
            archive.addClass(MSCBridgeActivator.class.getName());
            archive.addClass(SwarmSpringWebApplicationInitializer.class.getName());
            archive.as(ServiceActivatorArchive.class).addServiceActivator(MSCBridgeActivator.class.getName());

            archive.addAsServiceProvider(ServletContainerInitializer.class.getName(), SpringServletContainerInitializer.class.getName());

            archive.addModule("swarm.application", "flattish");

            new Thread(() -> {
                try {
                    System.err.println( "BEGIN WAR DEPLOY" );
                    deployer.deployAsync( archive, ()->{
                        System.err.println( "COMPLETED WAR DEPLOY" );
                    });
                    System.err.println( "END WAR DEPLOY" );
                    this.latch.countDown();
                } catch (DeploymentException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() throws EmbeddedServletContainerException {
        System.err.println("start web-app");
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            throw new EmbeddedServletContainerException("interrupted while starting", e);
        }
    }

    @Override
    public void stop() throws EmbeddedServletContainerException {
        System.err.println("stop web-app");
    }

    @Override
    public int getPort() {
        return 0;
    }
}
