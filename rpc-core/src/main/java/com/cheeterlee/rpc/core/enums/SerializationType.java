package com.cheeterlee.rpc.core.enums;

import lombok.Getter;

public enum SerializationType {

    /**
     * JDK algo
     */
    JDK((byte) 0),

    /**
     * JSON algo
     */
    JSON((byte) 1),

    /**
     * HESSIAN algo
     */
    HESSIAN((byte) 2),

    /**
     * KRYO algo
     */
    KRYO((byte) 3),

    /**
     * PROTOSTUFF algo
     */
    PROTOSTUFF((byte) 4);

    /**
     * type
     */
    @Getter
    private final byte type;

    SerializationType(byte type) {
        this.type = type;
    }

    public static SerializationType parseByName(String serializeName) {
        for (SerializationType serializationType : SerializationType.values()) {
            if (serializationType.name().equalsIgnoreCase(serializeName)) {
                return serializationType;
            }
        }
        return HESSIAN;
    }

    /**
     *  get enum type
     *
     * @param type type
     * @return enum type
     */
    public static SerializationType parseByType(byte type) {
        for (SerializationType serializationType : SerializationType.values()) {
            if (serializationType.getType() == type) {
                return serializationType;
            }
        }
        return HESSIAN;
    }
}
