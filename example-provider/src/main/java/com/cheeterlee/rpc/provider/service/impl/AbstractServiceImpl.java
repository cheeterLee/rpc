package com.cheeterlee.rpc.provider.service.impl;

import com.cheeterlee.rpc.api.service.AbstractService;
import com.cheeterlee.rpc.server.annotation.RpcService;

@RpcService(interfaceClass = AbstractService.class)
public class AbstractServiceImpl extends AbstractService {
    @Override
    public String abstractHello(String name) {
        return "abstract hello " + name;
    }
}