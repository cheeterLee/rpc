package com.cheeterlee.rpc.core.enums;

import lombok.Getter;

public enum MessageType {
    // request message
    REQUEST((byte) 0),

    // response message
    RESPONSE((byte) 1),

    HEARTBEAT_REQUEST((byte) 2),
    HEARTBEAT_RESPONSE((byte) 3);

    /**
     * 0 :{@link com.cheeterlee.rpc.core.common.RpcRequest}，
     * 1 : {@link com.cheeterlee.rpc.core.common.RpcResponse}
     */
    @Getter
    private final byte type;

    MessageType(byte type) {
        this.type = type;
    }

    /**
     * retrieve enum message type based on message type
     *
     * @param type message type
     * @return return enum message type
     * @throws IllegalArgumentException illegal message type
     */
    public static MessageType parseByType(byte type) throws IllegalArgumentException {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getType() == type) {
                return messageType;
            }
        }
        throw new IllegalArgumentException(String.format("The message type %s is illegal.", type));
    }
}

