package com.cheeterlee.rpc.server.store;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LocalServiceCache {

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static void addService(String serviceName, Object obj) {
        serviceMap.put(serviceName, obj);
        log.info("Service [{}] was successfully added to the local cache.", serviceName);
    }

    public static Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }

    public static void removeService(String serviceName) {
        serviceMap.remove(serviceName);
        log.info("Service [{}] was removed from local cache", serviceName);
    }

}
