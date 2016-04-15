package org.wildfly.swarm.springboot;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.bootstrap.util.BootstrapProperties;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.msc.internal.ServiceActivatorAsset;
import org.wildfly.swarm.spi.api.JARArchive;

/**
 * @author Bob McWhirter
 */
public class SpringApplication {

    public static void run(Object source, String... args) throws Exception {

        Class sourceClass = (Class) source;

        Swarm swarm = new Swarm();
        swarm.start(true);

        JARArchive archive = createArchive(swarm, sourceClass);

        swarm.deploy(archive);
    }

    public static JARArchive createArchive(Container container, Class source) {

        Archive defaultArchive = container.createDefaultDeployment();
        ServiceActivatorAsset activatorAsset = defaultArchive.as(ServiceActivatorArchive.class).getAsset();

        JARArchive archive = ShrinkWrap.create(JARArchive.class, "spring-boot-bootstrap.jar");
        archive.as(ServiceActivatorArchive.class).setAsset( activatorAsset );

        String structure = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jboss-deployment-structure>\n" +
                "    <deployment>\n" +
                "        <dependencies>\n" +
                "            <module name=\"swarm.application\" slot=\"flattish\" services=\"import\">\n" +
                "                <imports>\n" +
                "                  <include path=\"**\"/>\n" +
                "                </imports>\n" +
                "            </module>\n" +
                "        </dependencies>\n" +
                "    </deployment>\n" +
                "</jboss-deployment-structure>";

        archive.addAsResource(new StringAsset(structure), "META-INF/jboss-deployment-structure.xml");

        archive.as(ServiceActivatorArchive.class).addServiceActivator(MSCBridgeActivator.class);
        archive.as(ServiceActivatorArchive.class).addServiceActivator(SpringApplicationContextActivator.class);
        archive.addPackage(SpringApplicationContextActivator.class.getPackage());
        archive.addAsResource(new StringAsset(source.getName()), "spring-boot-class");

        System.setProperty(BootstrapProperties.APP_ARTIFACT, "spring-boot-bootstrap.jar" );

        return archive;
    }
}
