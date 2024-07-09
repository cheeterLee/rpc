package com.cheeterlee.rpc.core.registry;

import com.cheeterlee.rpc.core.common.ServiceInfo;
import com.cheeterlee.rpc.core.extension.SPI;

@SPI
public interface ServiceRegistry {

    /**
     * register a service to the registration center
     *
     * @param serviceInfo service info
     */
    void register(ServiceInfo serviceInfo) throws Exception;

    /**
     * unregister a service from the service center
     *
     * @param serviceInfo service info
     */
    void unregister(ServiceInfo serviceInfo) throws Exception;

    /**
     * close the connection with registration center
     */
    void destroy() throws Exception;

}