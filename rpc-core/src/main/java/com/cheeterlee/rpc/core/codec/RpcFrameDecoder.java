package com.cheeterlee.rpc.core.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class RpcFrameDecoder extends LengthFieldBasedFrameDecoder {

    public RpcFrameDecoder() {
        this(1024, 12, 4);
    }

    public RpcFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

}

