package org.wildfly.swarm.springboot.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.spi.context.ContainerContext;
import org.jboss.arquillian.container.spi.context.DeploymentContext;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentScenarioGenerator;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.wildfly.swarm.spi.api.JARArchive;
import org.wildfly.swarm.arquillian.adapter.MainSpecifier;
import org.wildfly.swarm.springboot.test.util.SpringBootMain;

/**
 * @author Bob McWhirter
 */
public class SpringBootDeploymentScenarioGenerator implements DeploymentScenarioGenerator {


    @Inject
    Instance<ContainerContext> containerContext;

    @Override
    public List<DeploymentDescription> generate(TestClass testClass) {
        JARArchive archive = ShrinkWrap.create(JARArchive.class);

        archive.as(ExplodedImporter.class)
                .importDirectory(new File("./target/classes"));

        archive.as(ExplodedImporter.class)
                .importDirectory(new File("./target/test-classes"));

        DeploymentDescription desc = new DeploymentDescription("spring-boot.jar", archive);

        this.containerContext.get().activate("daemon");

        archive.addClass(SpringBootMain.class);

        SpringApplicationConfiguration appConfigAnno = testClass.getAnnotation(SpringApplicationConfiguration.class);

        if  ( appConfigAnno != null ) {

            List<String> args = new ArrayList<>();

            String className = appConfigAnno.value()[0].getName();

            args.add( className );

            ActiveProfiles activeProfilesAnno = testClass.getAnnotation( ActiveProfiles.class);

            String[] profiles = null;

            if ( activeProfilesAnno != null ) {
                profiles = activeProfilesAnno.value();
            }

            if ( profiles == null ) {
                profiles = new String[]{};
            }

            for (String profile : profiles) {
                args.add( profile );
            }

            MainSpecifier mainSpec = new MainSpecifier(SpringBootMain.class.getName(), args.toArray( new String[ args.size() ] ) );
            this.containerContext.get().getObjectStore().add(MainSpecifier.class, mainSpec);
        }

        return Collections.singletonList(desc);
    }
}
