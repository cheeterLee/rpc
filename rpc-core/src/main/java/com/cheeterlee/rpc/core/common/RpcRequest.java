package com.cheeterlee.rpc.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {
    // name of requested service + version
    private String serviceName;
    // name of requested method
    private String method;
    // type of params
    private Class<?>[] parameterTypes;
    // params
    private Object[] parameterValues;
}
