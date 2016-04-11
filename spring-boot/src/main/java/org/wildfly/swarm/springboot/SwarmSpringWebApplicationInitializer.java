package org.wildfly.swarm.springboot;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.msc.service.ServiceName;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.web.WebApplicationInitializer;

/**
 * @author Bob McWhirter
 */
public class SwarmSpringWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletContextInitializer init = (ServletContextInitializer) ServiceRegistryActivator.REGISTRY.getService(ServiceName.of("swarm", "spring-boot", "init")).getValue();
        init.onStartup(servletContext);
    }

}
