package com.cheeterlee.rpc.core.protocol;

import lombok.Data;

@Data
public class RpcMessage {
    private MessageHeader header;
    private Object body;
}
