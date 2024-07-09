package com.cheeterlee.rpc.core.extension.factory;

import com.cheeterlee.rpc.core.extension.ExtensionFactory;
import com.cheeterlee.rpc.core.extension.ExtensionLoader;
import com.cheeterlee.rpc.core.extension.SPI;

public class SpiExtensionFactory implements ExtensionFactory {
    @Override
    public <T> T getExtension(Class<?> type, String name) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            ExtensionLoader<?> extensionLoader = ExtensionLoader.getExtensionLoader(type);
            // to do
        }
        return null;
    }
}