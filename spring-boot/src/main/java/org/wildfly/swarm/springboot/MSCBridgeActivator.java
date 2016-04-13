package org.wildfly.swarm.springboot;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceRegistry;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.msc.service.ServiceTarget;

/**
 * @author Bob McWhirter
 */
public class MSCBridgeActivator implements ServiceActivator {

    public static ServiceRegistry REGISTRY;
    public static ServiceTarget TARGET;

    @Override
    public void activate(ServiceActivatorContext context) throws ServiceRegistryException {
        REGISTRY = context.getServiceRegistry();
        TARGET = context.getServiceTarget();
    }
}
