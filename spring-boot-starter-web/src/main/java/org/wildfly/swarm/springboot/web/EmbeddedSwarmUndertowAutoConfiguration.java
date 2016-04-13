package org.wildfly.swarm.springboot.web;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;


/**
 * @author Bob McWhirter
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureBefore(name = "org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration")
public class EmbeddedSwarmUndertowAutoConfiguration {

    @Bean
    public EmbeddedServletContainerFactory wildflySwarmEmbeddedServletContainerFactory() {
        return new SwarmUndertowServletContainerFactory();
    }
}
