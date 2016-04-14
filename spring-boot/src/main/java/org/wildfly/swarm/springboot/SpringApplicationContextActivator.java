package org.wildfly.swarm.springboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceRegistryException;

/**
 * @author Bob McWhirter
 */
public class SpringApplicationContextActivator implements ServiceActivator {
    @Override
    public void activate(ServiceActivatorContext context) throws ServiceRegistryException {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream in = cl.getResourceAsStream("spring-boot-class");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            String className = reader.readLine();

            Class cls = cl.loadClass(className);

            SpringApplicationContextService.addService( context.getServiceTarget(), cl, cls );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
