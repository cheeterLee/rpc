package com.cheeterlee.rpc.core.constant;

import java.util.concurrent.atomic.AtomicInteger;

public class ProtocolConstants {
    private static final AtomicInteger ai = new AtomicInteger();

    // magic number to check if data is valid
    public static final byte[] MAGIC_NUM = new byte[]{(byte) 'w', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final byte VERSION = 1;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static int getSequenceId() {
        return ai.getAndIncrement();
    }

}

