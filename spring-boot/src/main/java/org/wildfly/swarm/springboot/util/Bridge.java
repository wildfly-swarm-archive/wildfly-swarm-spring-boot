package org.wildfly.swarm.springboot.util;

import java.util.ArrayList;
import java.util.List;

import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.ImmediateValue;
import org.wildfly.swarm.container.internal.Deployer;
import org.wildfly.swarm.springboot.MSCBridgeActivator;

/**
 * @author Bob McWhirter
 */
public class Bridge {
    private static final ServiceName DEPLOYER_SERVICE = ServiceName.of("swarm", "deployer");

    public static Deployer getDeployer() {
        return (Deployer) MSCBridgeActivator.REGISTRY.getService(DEPLOYER_SERVICE).getValue();
    }

    public static void addObject(Object object, String... nameParts) {
        ServiceName name = ServiceName.of(nameParts);
        addObject( object, name );
    }

    public static void addObject(Object object, ServiceName name) {
        MSCBridgeActivator.TARGET.addService(
                name,
                new ValueService<>(
                        new ImmediateValue<>(object)
                )
        ).install();
    }

    public static <T> List<T> getChildrenOf(ServiceName parentName, Class<T> type) {
        List<ServiceName> names = MSCBridgeActivator.REGISTRY.getServiceNames();

        List<T> objects = new ArrayList<>();

        for (ServiceName name : names) {
            if ( parentName.isParentOf( name ) ) {
                objects.add( (T) MSCBridgeActivator.REGISTRY.getService( name ).getValue() );
            }
        }

        return objects;
    }
}
