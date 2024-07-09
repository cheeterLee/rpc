package com.cheeterlee.rpc.core.util;

import com.cheeterlee.rpc.core.common.ServiceInfo;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;

public class ServiceUtil {
    public static final Gson gson = new Gson();


    /**
     * generates key based on service name + version
     *
     * @param serverName service name
     * @param version    version
     * @return key: serverName-version
     */
    public static String serviceKey(String serverName, String version) {

        return String.join("-", serverName, version);
    }

    /**
     * convert serviceInfo to map
     *
     * @param serviceInfo serviceInfo instance
     * @return Map
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map toMap(ServiceInfo serviceInfo) {
        if (serviceInfo == null) {
            return Collections.emptyMap();
        }
        Map map = gson.fromJson(gson.toJson(serviceInfo), Map.class);
        map.put("port", serviceInfo.getPort().toString());
        return map;
    }

    /**
     * convert map to serviceInfo instance
     *
     * @param map Map instance
     * @return serviceInfo instance
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static ServiceInfo toServiceInfo(Map map) {
        map.put("port", Integer.parseInt(map.getOrDefault("port", "0").toString()));
        return gson.fromJson(gson.toJson(map), ServiceInfo.class);
    }
}
