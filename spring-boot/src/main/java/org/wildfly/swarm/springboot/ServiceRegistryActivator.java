package org.wildfly.swarm.springboot;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.ServiceRegistryException;

/**
 * @author Bob McWhirter
 */
public class ServiceRegistryActivator implements ServiceActivator {

    public static ServiceRegistry REGISTRY;

    @Override
    public void activate(ServiceActivatorContext context) throws ServiceRegistryException {
        REGISTRY = context.getServiceRegistry();
    }
}
