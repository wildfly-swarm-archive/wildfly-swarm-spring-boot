package org.wildfly.swarm.springboot.web;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;

/**
 * @author Bob McWhirter
 */
public class Initializer implements ServletContainerInitializer {

    private final ServletContextInitializer[] initializers;

    public Initializer(ServletContextInitializer...initializers) {
        this.initializers = initializers;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        for (ServletContextInitializer each : this.initializers) {
            each.onStartup(servletContext);

        }
    }

    public InstanceFactory<Initializer> instanceFactory() {
        return new ImmediateInstanceFactory<>(this);
    }
}
