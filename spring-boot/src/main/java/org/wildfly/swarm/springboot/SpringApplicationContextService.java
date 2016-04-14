package org.wildfly.swarm.springboot;

import java.util.concurrent.CountDownLatch;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Bob McWhirter
 */
public class SpringApplicationContextService implements Service<ApplicationContext> {

    public static final ServiceName SERVICE_NAME = ServiceName.of("swarm", "spring-boot", "context");

    public static void addService(ServiceTarget target, ClassLoader classLoader, Class source) {
        target.addService(SERVICE_NAME, new SpringApplicationContextService(classLoader, source))
                .install();
    }

    private final ClassLoader classLoader;

    private final Class source;

    private ConfigurableApplicationContext context;

    private final CountDownLatch latch = new CountDownLatch(1);

    public SpringApplicationContextService(ClassLoader classLoader, Class source) {
        this.classLoader = classLoader;
        this.source = source;
    }

    @Override
    public void start(StartContext context) throws StartException {

        context.asynchronous();

        //Thread thread = new Thread(() -> {
        Thread.currentThread().setContextClassLoader(this.classLoader);
        try {
            System.err.println("-----> START ");
            context.complete();
            this.context = org.springframework.boot.SpringApplication.run(new Object[]{
                            this.source
                    },
                    new String[]{});

            this.latch.countDown();
            System.err.println("-----> STARTED " + this.context);
        } catch (Throwable t) {
            context.failed(new StartException(t));
            t.printStackTrace();
        }

        //thread.setName("spring-boot");
        //thread.setContextClassLoader(this.classLoader);
        //thread.start();
    }

    @Override
    public void stop(StopContext context) {
        this.context.stop();
    }

    @Override
    public ApplicationContext getValue() throws IllegalStateException, IllegalArgumentException {
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.context;
    }
}
