package org.wildfly.swarm.springboot.web;

import javax.servlet.ServletContainerInitializer;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.web.SpringServletContainerInitializer;
import org.wildfly.swarm.container.internal.Deployer;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.springboot.MSCBridgeActivator;
import org.wildfly.swarm.springboot.util.Bridge;
import org.wildfly.swarm.undertow.WARArchive;

/**
 * @author Bob McWhirter
 */
public class SwarmUndertowServletContainerFactory implements EmbeddedServletContainerFactory {

    @Override
    public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
        for (ServletContextInitializer each : initializers) {
            int counter = 0;
            Bridge.addObject(each, SwarmSpringWebApplicationInitializer.INIT.append("" + (++counter)));
        }

        WARArchive archive = ShrinkWrap.create(WARArchive.class, "spring-boot-app.war");
        try {
            archive.addClass(MSCBridgeActivator.class.getName());
            archive.addClass(SwarmSpringWebApplicationInitializer.class.getName());
            archive.as(ServiceActivatorArchive.class).addServiceActivator(MSCBridgeActivator.class.getName());

            archive.addAsServiceProvider(ServletContainerInitializer.class.getName(), SpringServletContainerInitializer.class.getName());

            archive.addModule("swarm.application", "flattish");

            Deployer deployer = Bridge.getDeployer();
            deployer.deploy(archive);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new EmbeddedServletContainer() {
            @Override
            public void start() throws EmbeddedServletContainerException {

            }

            @Override
            public void stop() throws EmbeddedServletContainerException {

            }

            @Override
            public int getPort() {
                return 0;
            }
        };
    }
}
