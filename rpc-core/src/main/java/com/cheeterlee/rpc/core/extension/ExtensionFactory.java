package com.cheeterlee.rpc.core.extension;

@SPI
public interface ExtensionFactory {

    /**
     * obtain extension object instance
     *
     * @param type object type
     * @param name object name
     * @param <T>  instance type
     * @return object instance
     */
    <T> T getExtension(Class<?> type, String name);
}
