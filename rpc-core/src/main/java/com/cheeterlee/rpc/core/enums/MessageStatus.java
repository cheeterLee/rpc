package com.cheeterlee.rpc.core.enums;

import lombok.Getter;

public enum MessageStatus {


    SUCCESS((byte) 0),
    FAIL((byte) 1);

    @Getter
    private final byte code;

    MessageStatus(byte code) {
        this.code = code;
    }

    public static boolean isSuccess(byte code) {
        return MessageStatus.SUCCESS.code == code;
    }

}

