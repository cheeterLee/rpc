package com.cheeterlee.rpc.core.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class RpcResponse implements Serializable {

    // return value of request
    private Object returnValue;

    // exception when error occurs
    private Exception exceptionValue;

}

