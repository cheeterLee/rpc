package com.cheeterlee.rpc.core.loadbalance;

import com.cheeterlee.rpc.core.common.RpcRequest;
import com.cheeterlee.rpc.core.common.ServiceInfo;

import java.util.List;

public interface LoadBalance {

    /**
     * return one instance based on strategy
     *
     * @param invokers list of services
     * @param request rpc request
     * @return return service object according to strategy
     */
    ServiceInfo select(List<ServiceInfo> invokers, RpcRequest request);
}