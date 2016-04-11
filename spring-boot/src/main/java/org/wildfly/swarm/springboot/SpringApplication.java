package org.wildfly.swarm.springboot;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.msc.ServiceActivatorArchive;
import org.wildfly.swarm.spi.api.JARArchive;

/**
 * @author Bob McWhirter
 */
public class SpringApplication {

    public static void run(Object source, String... args) throws Exception {

        Class sourceClass = (Class) source;

        Swarm swarm = new Swarm();
        swarm.start();

        JARArchive archive = ShrinkWrap.create(JARArchive.class, "spring-boot-bootstrap.jar");

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

        archive.addAsResource( new StringAsset(structure), "META-INF/jboss-deployment-structure.xml" );

        archive.as(ServiceActivatorArchive.class).addServiceActivator( SpringApplicationActivator.class );
        archive.addPackage(SpringApplicationActivator.class.getPackage());
        archive.addAsResource( new StringAsset( sourceClass.getName() ), "spring-boot-class");

        swarm.deploy( archive );


    }
}
