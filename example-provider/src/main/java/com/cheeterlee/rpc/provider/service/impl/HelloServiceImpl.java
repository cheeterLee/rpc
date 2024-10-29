package com.cheeterlee.rpc.provider.service.impl;

import com.cheeterlee.rpc.api.service.HelloService;
import com.cheeterlee.rpc.server.annotation.RpcService;

@RpcService(interfaceClass = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}