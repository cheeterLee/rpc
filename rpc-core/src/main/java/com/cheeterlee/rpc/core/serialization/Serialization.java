package com.cheeterlee.rpc.core.serialization;

import com.cheeterlee.rpc.core.extension.SPI;

@SPI
public interface Serialization {

    /**
     * serialize
     *
     * @param object obj
     * @param <T>    obj type
     * @return byte array after serialization
     */
    <T> byte[] serialize(T object);

    /**
     * deserialize
     *
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

}
