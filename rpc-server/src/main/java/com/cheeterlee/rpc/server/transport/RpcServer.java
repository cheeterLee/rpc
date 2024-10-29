package com.cheeterlee.rpc.server.transport;

public interface RpcServer {

    /**
     * 开启 RpcServer 服务
     *
     * @param port 启动端口
     */
    void start(Integer port);

}
