package com.cheeterlee.rpc.core.discovery;

import com.cheeterlee.rpc.core.common.RpcRequest;
import com.cheeterlee.rpc.core.common.ServiceInfo;
import com.cheeterlee.rpc.core.extension.SPI;

import java.util.ArrayList;
import java.util.List;

@SPI
public interface ServiceDiscovery {

    /**
     * service discovery
     *
     * @param request rpc quest with service name
     * @return info of service provider
     */
    ServiceInfo discover(RpcRequest request);

    /**
     * return all providers for service
     *
     * @param serviceName service name
     * @return all the service providers
     */
    default List<ServiceInfo> getServices(String serviceName) throws Exception {

        return new ArrayList<>();
    }

    /**
     * close connection with service center
     */
    void destroy() throws Exception;

}
