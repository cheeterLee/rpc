package com.cheeterlee.rpc.server.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcService {

    /**
     * interface type
     */
    Class<?> interfaceClass() default void.class;

    /**
     * interface name
     */
    String interfaceName() default "";

    /**
     * version
     */
    String version() default "1.0";

}

