package org.wildfly.swarm.springboot.test;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.CachedAuxilliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.wildfly.swarm.springboot.MSCBridgeActivator;
import org.wildfly.swarm.springboot.test.remote.SpringBootEnricherRemoteExtension;
import org.wildfly.swarm.springboot.test.remote.SpringBootTestEnricher;

/**
 * @author Bob McWhirter
 */
public class SpringBootEnricherArchiveAppender extends CachedAuxilliaryArchiveAppender {

    @Override
    protected Archive<?> buildArchive() {
        return ShrinkWrap.create(JavaArchive.class, "swarm-testenricher-springboot.jar")
                .addPackage(SpringBootTestEnricher.class.getPackage())
                .addAsServiceProvider(RemoteLoadableExtension.class, SpringBootEnricherRemoteExtension.class);
    }
}
