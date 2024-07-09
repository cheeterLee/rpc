package com.cheeterlee.rpc.core.loadbalance.impl;

import com.cheeterlee.rpc.core.common.RpcRequest;
import com.cheeterlee.rpc.core.common.ServiceInfo;
import com.cheeterlee.rpc.core.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {

    final Random random = new Random();

    @Override
    protected ServiceInfo doSelect(List<ServiceInfo> invokers, RpcRequest request) {
        return invokers.get(random.nextInt(invokers.size()));
    }
}
