package com.cheeterlee.rpc.core.serialization;

import com.cheeterlee.rpc.core.enums.SerializationType;
import com.cheeterlee.rpc.core.serialization.hessian.HessianSerialization;
import com.cheeterlee.rpc.core.serialization.jdk.JdkSerialization;
import com.cheeterlee.rpc.core.serialization.json.JsonSerialization;
import com.cheeterlee.rpc.core.serialization.kryo.KryoSerialization;
import com.cheeterlee.rpc.core.serialization.protostuff.ProtostuffSerialization;

public class SerializationFactory {

    public static Serialization getSerialization(SerializationType enumType) {
        switch (enumType) {
            case JDK:
                return new JdkSerialization();
            case JSON:
                return new JsonSerialization();
            case HESSIAN:
                return new HessianSerialization();
            case KRYO:
                return new KryoSerialization();
            case PROTOSTUFF:
                return new ProtostuffSerialization();
            default:
                throw new IllegalArgumentException(String.format("The serialization type %s is illegal.",
                        enumType.name()));
        }
    }

}
