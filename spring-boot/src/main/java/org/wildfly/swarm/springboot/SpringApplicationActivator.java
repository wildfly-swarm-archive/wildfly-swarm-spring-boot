package org.wildfly.swarm.springboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceRegistryException;

/**
 * @author Bob McWhirter
 */
public class SpringApplicationActivator implements ServiceActivator {

    @Override
    public void activate(ServiceActivatorContext context) throws ServiceRegistryException {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        InputStream in = cl.getResourceAsStream("spring-boot-class");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            String className = reader.readLine();

            Class cls = cl.loadClass(className);

            Thread thread = new Thread(() -> {
                try {
                    org.springframework.boot.SpringApplication.run(new Object[]{
                                    cls
                            },
                            new String[]{});
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });

            thread.setName("spring-boot");
            thread.setContextClassLoader(cl);

            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
