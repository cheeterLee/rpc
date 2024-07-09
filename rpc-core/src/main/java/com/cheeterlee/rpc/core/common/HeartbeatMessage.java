package com.cheeterlee.rpc.core.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class HeartbeatMessage implements Serializable {
    private String msg;
}
