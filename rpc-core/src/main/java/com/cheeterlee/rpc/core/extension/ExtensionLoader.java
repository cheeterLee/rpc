package com.cheeterlee.rpc.core.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ExtensionLoader<T> {

    /**
     * directory
     */
    private static final String SERVICES_DIRECTORY = "META-INF/extensions/";

    /**
     * cache，key - class，val - extension loader
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;

    private final ExtensionFactory objectFactory;


    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    private final Holder<Object> cachedAdaptiveInstance = new Holder<>();

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
        objectFactory = null;
    }

    /**
     *  get loader
     *
     * @param type  type
     * @param <T>  service class
     * @return extension loader
     */
    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException(String.format("Extension type (%s) is not an interface!", type));
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException(String.format("Extension type (%s) is not an extension, " + "because it is NOT annotated with @%s!", type, SPI.class.getSimpleName()));
        }
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    /**
     *  key -> instance
     *
     * @param name name
     * @return extension instance
     */
    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name == null.");
        }
        // get holder from cache
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        // get instance from Holder
        Object instance = holder.get();
        if (instance == null) {
            // lock
            synchronized (holder) {
                // retrieve again
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * name -> instance
     *
     * @param name name
     * @return instance
     */
    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new IllegalArgumentException("No such extension name " + name);
        }
        // get instance
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        // if null, create
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Failed to create extension instance.", e);
            }
        }
        return instance;
    }

    /**
     * get extension classes
     */
    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * load all extension class for current interface under specific directory
     *
     * @param extensionClasses all extension type class map
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        // join file name
        String filename = SERVICES_DIRECTORY + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(filename);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            log.debug("Failed to load directory.", e);
        }
    }

    /**
     * load extension class under a specific url
     *
     * @param extensionClasses extension class cache
     * @param classLoader      class loader
     * @param resourceUrl      resource url
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8))) {
            String line;
            // read by line
            while ((line = reader.readLine()) != null) {
                // get first index
                final int ci = line.indexOf("#");
                if (ci >= 0) {
                    // filter comments
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        // get first index of =
                        final int i = line.indexOf("=");
                        String name = line.substring(0, i).trim();
                        String className = line.substring(i + 1).trim();
                        if (name.length() > 0 && className.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(className);
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Failed to load extension class (interface: " + type + ", class line: " + line + ") in "
                                + resourceUrl + ", cause: " + e.getMessage(), e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Exception occurred when loading extension class (interface: " +
                    type + ", class file: " + resourceUrl + ") in " + resourceUrl, e);
            throw new RuntimeException(e);
        }
    }
}
