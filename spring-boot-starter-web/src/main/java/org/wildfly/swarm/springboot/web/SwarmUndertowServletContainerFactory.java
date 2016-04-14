package org.wildfly.swarm.springboot.web;

import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.wildfly.swarm.springboot.util.Bridge;

/**
 * @author Bob McWhirter
 */
public class SwarmUndertowServletContainerFactory implements EmbeddedServletContainerFactory {

    @Override
    public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
        return new SwarmUndertowServletContainer(Bridge.getDeployer(), initializers );
    }
}
