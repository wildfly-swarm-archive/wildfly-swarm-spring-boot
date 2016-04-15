package org.wildfly.swarm.springboot.test;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * @author Bob McWhirter
 */
public class SpringBootEnricherExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, SpringBootEnricherArchiveAppender.class );
    }
}
