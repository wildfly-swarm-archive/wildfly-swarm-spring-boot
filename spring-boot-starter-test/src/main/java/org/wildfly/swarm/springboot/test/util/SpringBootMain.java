package org.wildfly.swarm.springboot.test.util;

import java.util.Arrays;

import org.wildfly.swarm.springboot.SpringApplication;

/**
 * @author Bob McWhirter
 */
public class SpringBootMain {

    public static void main(String...args) throws Exception {
        String sourceClassName = args[0];
        if ( args.length > 1 ) {
            String[] activeProfiles = Arrays.copyOfRange(args, 1, args.length);
            System.setProperty( "spring.profiles.active", String.join( ",", activeProfiles ) );
        }
        Class source = Class.forName( sourceClassName );
        SpringApplication.run( source, new String[]{});

    }
}
