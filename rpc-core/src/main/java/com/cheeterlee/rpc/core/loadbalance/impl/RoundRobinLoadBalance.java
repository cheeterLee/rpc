package com.cheeterlee.rpc.core.loadbalance.impl;

import com.cheeterlee.rpc.core.common.RpcRequest;
import com.cheeterlee.rpc.core.common.ServiceInfo;
import com.cheeterlee.rpc.core.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public ServiceInfo doSelect(List<ServiceInfo> invokers, RpcRequest request) {
        return invokers.get(getAndIncrement() % invokers.size());
    }

    /**
     * @return curr int
     */
    public final int getAndIncrement() {
        int prev, next;
        do {
            prev = atomicInteger.get();
            next = prev == Integer.MAX_VALUE ? 0 : prev + 1;
        } while (!atomicInteger.compareAndSet(prev, next));
        return prev;
    }

}