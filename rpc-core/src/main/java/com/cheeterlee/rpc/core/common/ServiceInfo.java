package com.cheeterlee.rpc.core.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo implements Serializable {

    // app name
    private String appName;

    // service name + version
    private String serviceName;

    // version
    private String version;

    // service provider address
    private String address;

    // service provider port
    private Integer port;
}

