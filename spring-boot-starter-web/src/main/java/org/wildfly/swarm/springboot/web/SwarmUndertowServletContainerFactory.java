package org.wildfly.swarm.springboot.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import io.undertow.servlet.api.DeploymentInfo;
import org.springframework.boot.context.embedded.AbstractEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.util.Assert;

/**
 * @author Bob McWhirter
 */
public class SwarmUndertowServletContainerFactory extends AbstractEmbeddedServletContainerFactory {

    private ArrayList<UndertowDeploymentInfoCustomizer> deploymentInfoCustomizers = new ArrayList<>();

    @Override
    public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
        ServletContextInitializer[] mergedInitializers = mergeInitializers(initializers);
        return new SwarmUndertowServletContainer(this, mergedInitializers);
    }

    /**
     * Set {@link UndertowDeploymentInfoCustomizer}s that should be applied to the
     * Undertow {@link DeploymentInfo}. Calling this method will replace any existing
     * customizers.
     *
     * @param customizers the customizers to set
     */
    public void setDeploymentInfoCustomizers(Collection<? extends UndertowDeploymentInfoCustomizer> customizers) {
        System.err.println("set deployment info customers: " + customizers);
        Assert.notNull(customizers, "Customizers must not be null");
        this.deploymentInfoCustomizers = new ArrayList<UndertowDeploymentInfoCustomizer>(
                customizers);
    }

    /**
     * Returns a mutable collection of the {@link UndertowDeploymentInfoCustomizer}s that
     * will be applied to the Undertow {@link DeploymentInfo} .
     *
     * @return the customizers that will be applied
     */
    public Collection<UndertowDeploymentInfoCustomizer> getDeploymentInfoCustomizers() {
        return this.deploymentInfoCustomizers;
    }

    /**
     * Add {@link UndertowDeploymentInfoCustomizer}s that should be used to customize the
     * Undertow {@link DeploymentInfo}.
     *
     * @param customizers the customizers to add
     */
    public void addDeploymentInfoCustomizers(UndertowDeploymentInfoCustomizer... customizers) {
        System.err.println("add deployment info customers: " + Arrays.asList(customizers));
        Assert.notNull(customizers, "UndertowDeploymentInfoCustomizers must not be null");
        this.deploymentInfoCustomizers.addAll(Arrays.asList(customizers));
    }

    ResourceManager getDocumentRootResourceManager() {
        File root = getCanonicalDocumentRoot();
        if (root.isDirectory()) {
            return new FileResourceManager(root, 0);
        }
        if (root.isFile()) {
            return new JarResourceManager(root);
        }
        return ResourceManager.EMPTY_RESOURCE_MANAGER;
    }

    /**
     * Return the document root in canonical form. Undertow uses File#getCanonicalFile()
     * to determine whether a resource has been requested using the proper case but on
     * Windows {@code java.io.tmpdir} may be set as a tilde-compressed pathname.
     *
     * @return the canonical document root
     */
    private File getCanonicalDocumentRoot() {
        try {
            File root = getValidDocumentRoot();
            root = (root != null ? root : createTempDir("undertow-docbase"));
            return root.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot get canonical document root", e);
        }
    }

    /**
     * Undertow {@link ResourceManager} for JAR resources.
     */
    private static class JarResourceManager implements ResourceManager {

        private final String jarPath;

        JarResourceManager(File jarFile) {
            this(jarFile.getAbsolutePath());
        }

        JarResourceManager(String jarPath) {
            this.jarPath = jarPath;
        }

        @Override
        public Resource getResource(String path) throws IOException {
            URL url = new URL("jar:file:" + this.jarPath + "!" + path);
            URLResource resource = new URLResource(url, url.openConnection(), path);
            if (resource.getContentLength() < 0) {
                return null;
            }
            return resource;
        }

        @Override
        public boolean isResourceChangeListenerSupported() {
            return false;
        }

        @Override
        public void registerResourceChangeListener(ResourceChangeListener listener) {
            throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();

        }

        @Override
        public void removeResourceChangeListener(ResourceChangeListener listener) {
            throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
        }

        @Override
        public void close() throws IOException {
        }

    }
}
