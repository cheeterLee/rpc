package com.cheeterlee.rpc.client.transport.http;


import com.cheeterlee.rpc.client.common.RequestMetadata;
import com.cheeterlee.rpc.client.transport.RpcClient;
import com.cheeterlee.rpc.core.common.RpcResponse;
import com.cheeterlee.rpc.core.protocol.RpcMessage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRpcClient implements RpcClient {

    @Override
    public RpcMessage sendRpcRequest(RequestMetadata requestMetadata) {
        try {
            URL url = new URL("http", requestMetadata.getServerAddr(), requestMetadata.getPort(), "/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            // 配置
            OutputStream os = httpURLConnection.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);

            // 发送 RpcRequest 对象
            oos.writeObject(requestMetadata.getRpcMessage().getBody());
            oos.flush();
            oos.close();

            // 构造 RpcMessage 对象
            RpcMessage rpcMessage = new RpcMessage();

            InputStream is = httpURLConnection.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            // 阻塞读取
            RpcResponse response = (RpcResponse) ois.readObject();
            rpcMessage.setBody(response);
            return rpcMessage;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
