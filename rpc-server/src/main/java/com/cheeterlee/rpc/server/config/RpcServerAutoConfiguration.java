package com.cheeterlee.rpc.server.config;


import com.cheeterlee.rpc.core.registry.ServiceRegistry;
import com.cheeterlee.rpc.core.registry.zk.ZookeeperServiceRegistry;
import com.cheeterlee.rpc.server.spring.RpcServerBeanPostProcessor;
import com.cheeterlee.rpc.server.transport.RpcServer;
import com.cheeterlee.rpc.server.transport.http.HttpRpcServer;
import com.cheeterlee.rpc.server.transport.netty.NettyRpcServer;
import com.cheeterlee.rpc.server.transport.socket.SocketRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcServerAutoConfiguration {

    @Autowired
    RpcServerProperties properties;

    /**
     * 创建 ServiceRegistry 实例 bean，当没有配置时默认使用 zookeeper 作为配置中心
     */
    @Bean(name = "serviceRegistry")
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.server", name = "registry", havingValue = "zookeeper", matchIfMissing = true)
    public ServiceRegistry zookeeperServiceRegistry() {
        return new ZookeeperServiceRegistry(properties.getRegistryAddr());
    }


    // 当没有配置通信协议属性时，默认使用 netty 作为通讯协议
    @Bean(name = "rpcServer")
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.server", name = "transport", havingValue = "netty", matchIfMissing = true)
    public RpcServer nettyRpcServer() {
        return new NettyRpcServer();
    }

    @Bean(name = "rpcServer")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.server", name = "transport", havingValue = "http")
    @ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat"})
    public RpcServer httpRpcServer() {
        return new HttpRpcServer();
    }

    @Bean(name = "rpcServer")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.server", name = "transport", havingValue = "socket")
    public RpcServer socketRpcServer() {
        return new SocketRpcServer();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({ServiceRegistry.class, RpcServer.class})
    public RpcServerBeanPostProcessor rpcServerBeanPostProcessor(@Autowired ServiceRegistry serviceRegistry,
                                                                 @Autowired RpcServer rpcServer,
                                                                 @Autowired RpcServerProperties properties) {
        return new RpcServerBeanPostProcessor(serviceRegistry, rpcServer, properties);
    }

}
