package com.cheeterlee.rpc.core.loadbalance;

import com.cheeterlee.rpc.core.common.RpcRequest;
import com.cheeterlee.rpc.core.common.ServiceInfo;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public ServiceInfo select(List<ServiceInfo> invokers, RpcRequest request) {
        if (invokers == null || invokers.isEmpty()) {
            return null;
        }
        // if only one service, no need for load balancing
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        // make the selection
        return doSelect(invokers, request);
    }

    /**
     * selection
     *
     * @param invokers list of services
     * @param request  rpc request
     * @return service info
     */
    protected abstract ServiceInfo doSelect(List<ServiceInfo> invokers, RpcRequest request);

}
