package org.wildfly.swarm.springboot.test.remote;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.TestEnricher;

/**
 * @author Bob McWhirter
 */
public class SpringBootEnricherRemoteExtension implements RemoteLoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(TestEnricher.class, SpringBootTestEnricher.class);
    }
}
