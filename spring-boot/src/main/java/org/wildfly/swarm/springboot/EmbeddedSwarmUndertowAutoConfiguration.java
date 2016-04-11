package org.wildfly.swarm.springboot;

import javax.servlet.ServletContainerInitializer;

import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.ImmediateValue;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerException;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.SpringServletContainerInitializer;
import org.wildfly.swarm.container.internal.Deployer;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.undertow.WARArchive;

/**
 * @author Bob McWhirter
 */
@Configuration
public class EmbeddedSwarmUndertowAutoConfiguration {

    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
        return initializers -> {
            for (ServletContextInitializer each : initializers) {
                SpringApplicationActivator.TARGET.addService(ServiceName.of("swarm", "spring-boot", "init"),
                        new ValueService<>(new ImmediateValue<>(each)))
                        .install();
            }

            WARArchive archive = ShrinkWrap.create(WARArchive.class, "spring-boot-app.war");
            try {
                archive.addClass(ServiceRegistryActivator.class.getName());
                archive.addClass(SwarmSpringWebApplicationInitializer.class.getName());
                archive.as(ServiceActivatorArchive.class).addServiceActivator(ServiceRegistryActivator.class.getName());

                archive.addAsServiceProvider(ServletContainerInitializer.class.getName(), SpringServletContainerInitializer.class.getName());

                archive.addModule("swarm.application", "flattish");

                Deployer deployer = (Deployer) SpringApplicationActivator.REGISTRY.getService(ServiceName.of("swarm", "deployer")).getValue();
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
        };
    }
}
