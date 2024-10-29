package com.cheeterlee.rpc.client.transport;


import com.cheeterlee.rpc.client.common.RequestMetadata;
import com.cheeterlee.rpc.core.protocol.RpcMessage;

public interface RpcClient {

    /**
     * 发起远程过程调用
     *
     * @param requestMetadata rpc 请求元数据
     * @return 响应结果
     */
    RpcMessage sendRpcRequest(RequestMetadata requestMetadata);

}
