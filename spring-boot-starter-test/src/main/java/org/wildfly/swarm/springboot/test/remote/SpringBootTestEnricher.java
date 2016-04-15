package org.wildfly.swarm.springboot.test.remote;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.jboss.arquillian.core.spi.context.Context;
import org.jboss.arquillian.test.spi.TestEnricher;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.wildfly.swarm.springboot.SpringApplicationContextService;
import org.wildfly.swarm.springboot.util.Bridge;

/**
 * @author Bob McWhirter
 */
public class SpringBootTestEnricher implements TestEnricher {

    @Override
    public void enrich(Object testCase) {

        ConfigurableApplicationContext applicationCtx = Bridge.lookup(SpringApplicationContextService.SERVICE_NAME );

        if (applicationCtx != null) {
            // NOTE for annotation-based injection, we can simply use autowireBean(testCase); also takes care of ApplicationContextAware
            applicationCtx.getBeanFactory().autowireBeanProperties(testCase, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
            Iterator<String> iter = applicationCtx.getBeanFactory().getBeanNamesIterator();
        } else {
            System.err.println("ConfigurableApplicationContext not found in Arquillian context. Spring injections will be skipped.");
        }
    }

    @Override
    public Object[] resolve(Method method) {
        return new Object[0];
    }
}
