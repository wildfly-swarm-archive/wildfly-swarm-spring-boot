package org.wildfly.swarm.springboot.test;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * @author Bob McWhirter
 */
public class SpringBootExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(DeploymentScenarioGenerator.class, SpringBootDeploymentScenarioGenerator.class);
        builder.service(AuxiliaryArchiveAppender.class, SpringBootEnricherArchiveAppender.class );
    }
}
