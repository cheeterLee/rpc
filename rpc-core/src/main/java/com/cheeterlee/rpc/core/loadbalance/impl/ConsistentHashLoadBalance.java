package com.cheeterlee.rpc.core.loadbalance.impl;

import com.cheeterlee.rpc.core.common.RpcRequest;
import com.cheeterlee.rpc.core.common.ServiceInfo;
import com.cheeterlee.rpc.core.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    private final Map<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    public ServiceInfo doSelect(List<ServiceInfo> invokers, RpcRequest request) {
        String method = request.getMethod();
        String key = request.getServiceName() + "." + method;
        int identityHashCode = System.identityHashCode(invokers);
        ConsistentHashSelector selector = selectors.get(key);
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(key, new ConsistentHashSelector(invokers, 160, identityHashCode));
            selector = selectors.get(key);
        }
        String selectKey = key;
        if (request.getParameterValues() != null && request.getParameterValues().length > 0) {
            selectKey += Arrays.stream(request.getParameterValues());
        }
        return selector.select(selectKey);
    }

    private final static class ConsistentHashSelector {

        /**
         * tree map store nodes
         */
        private final TreeMap<Long, ServiceInfo> virtualInvokers;

        /**
         * invokers original hash code
         */
        private final int identityHashCode;

        /**
         * build ConsistentHashSelector obj
         *
         * @param invokers         virtual nodes
         * @param replicaNumber   default 160
         * @param identityHashCode invokers original hash
         */
        public ConsistentHashSelector(List<ServiceInfo> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for (ServiceInfo invoker : invokers) {
                String address = invoker.getAddress();
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(address + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        /**
         * md5 calculation
         *
         * @param key  key
         * @return 16 byte arr
         */
        private byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            return md.digest();
        }

        /**
         * generate hash
         *
         * @param digest md5
         * @param number number of indexes
         * @return hash val
         */
        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        public ServiceInfo select(String key) {
            byte[] digest = md5(key);
            return selectForKey(hash(digest, 0));
        }

        /**
         * get the first service >= hash val
         *
         * @param hash hash val
         * @return service info
         */
        private ServiceInfo selectForKey(long hash) {
            // find first invoker in treemap that node val >= hash
            Map.Entry<Long, ServiceInfo> entry = virtualInvokers.ceilingEntry(hash);
            // if hash > largest, assign treemap first node for entry
            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }
    }
}
