package org.wildfly.swarm.springboot.web;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.jboss.msc.service.ServiceName;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.wildfly.swarm.springboot.util.Bridge;

/**
 * @author Bob McWhirter
 */
public class SwarmSpringWebApplicationInitializer implements WebApplicationInitializer {

    static final ServiceName INIT = ServiceName.of( "swarm", "spring-boot", "web-app-initializer" );

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        List<ServletContextInitializer> initializers = Bridge.getChildrenOf(INIT, ServletContextInitializer.class);
        for (ServletContextInitializer initializer : initializers) {
            initializer.onStartup( servletContext );
        }
    }

}
