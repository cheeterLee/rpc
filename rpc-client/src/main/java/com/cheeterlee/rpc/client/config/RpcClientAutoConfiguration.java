package com.cheeterlee.rpc.client.config;

import com.cheeterlee.rpc.client.proxy.ClientStubProxyFactory;
import com.cheeterlee.rpc.client.spring.RpcClientBeanPostProcessor;
import com.cheeterlee.rpc.client.spring.RpcClientExitDisposableBean;
import com.cheeterlee.rpc.client.transport.http.HttpRpcClient;
import com.cheeterlee.rpc.client.transport.netty.NettyRpcClient;
import com.cheeterlee.rpc.client.transport.socket.SocketRpcClient;
import com.cheeterlee.rpc.core.discovery.ServiceDiscovery;
import com.cheeterlee.rpc.core.discovery.zk.ZookeeperServiceDiscovery;
import com.cheeterlee.rpc.core.loadbalance.LoadBalance;
import com.cheeterlee.rpc.core.loadbalance.impl.ConsistentHashLoadBalance;
import com.cheeterlee.rpc.core.loadbalance.impl.RandomLoadBalance;
import com.cheeterlee.rpc.core.loadbalance.impl.RoundRobinLoadBalance;
import com.cheeterlee.rpc.client.transport.RpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
public class RpcClientAutoConfiguration {

    /**
     * 属性绑定的实现方式二：
     * - 创建 RpcClientProperties 对象，绑定到配置文件
     * - 如果使用此方法，可以直接给属性赋初始值
     *
     * @param environment 当前应用的环境（支持 yaml、properties 等文件格式）
     * @return 返回对应的绑定属性类
     * @deprecated 弃用，使用被 {@link org.springframework.boot.context.properties.ConfigurationProperties} 标注的属性类代替，
     * 生成 metadata。
     */
//    @Bean
    @Deprecated
    public RpcClientProperties rpcClientProperties(Environment environment) {
        // 获取绑定器，将对应的属性绑定到指定类上
        BindResult<RpcClientProperties> bind = Binder.get(environment).bind("rpc.client", RpcClientProperties.class);
        // 获取实例
        return bind.get();
    }

    @Autowired
    RpcClientProperties rpcClientProperties;

    @Bean(name = "loadBalance")
    @Primary
    @ConditionalOnMissingBean // 不指定 value 则值默认为当前创建的类
    @ConditionalOnProperty(prefix = "rpc.client", name = "loadbalance", havingValue = "random", matchIfMissing = true)
    public LoadBalance randomLoadBalance() {
        return new RandomLoadBalance();
    }

    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "loadbalance", havingValue = "roundRobin")
    public LoadBalance roundRobinLoadBalance() {
        return new RoundRobinLoadBalance();
    }

    @Bean(name = "loadBalance")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "loadbalance", havingValue = "consistentHash")
    public LoadBalance consistentHashLoadBalance() {
        return new ConsistentHashLoadBalance();
    }

    @Bean(name = "serviceDiscovery")
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnBean(LoadBalance.class)
    @ConditionalOnProperty(prefix = "rpc.client", name = "registry", havingValue = "zookeeper", matchIfMissing = true)
    public ServiceDiscovery zookeeperServiceDiscovery(@Autowired LoadBalance loadBalance) {
        return new ZookeeperServiceDiscovery(rpcClientProperties.getRegistryAddr(), loadBalance);
    }

    @Bean(name = "rpcClient")
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "transport", havingValue = "netty", matchIfMissing = true)
    public RpcClient nettyRpcClient() {
        return new NettyRpcClient();
    }

    @Bean(name = "rpcClient")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "transport", havingValue = "http")
    public RpcClient httpRpcClient() {
        return new HttpRpcClient();
    }

    @Bean(name = "rpcClient")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpc.client", name = "transport", havingValue = "socket")
    public RpcClient socketRpcClient() {
        return new SocketRpcClient();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({ServiceDiscovery.class, RpcClient.class})
    public ClientStubProxyFactory clientStubProxyFactory(@Autowired ServiceDiscovery serviceDiscovery,
                                                         @Autowired RpcClient rpcClient,
                                                         @Autowired RpcClientProperties rpcClientProperties) {
        return new ClientStubProxyFactory(serviceDiscovery, rpcClient, rpcClientProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(@Autowired ClientStubProxyFactory clientStubProxyFactory) {
        return new RpcClientBeanPostProcessor(clientStubProxyFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcClientExitDisposableBean rpcClientExitDisposableBean(@Autowired ServiceDiscovery serviceDiscovery) {
        return new RpcClientExitDisposableBean(serviceDiscovery);
    }

}
