package com.cml.component.distribute.lock.core;

import java.lang.annotation.*;

/**
 * @Auther: cml
 * @Date: 2018-09-19 16:48
 * @Description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributeLock {
    /**
     * 锁的key，支持el表达式,#arg0表示第一个参数，其他参数以此类推
     *
     * @return
     */
    String key() default "";

    /**
     * 模块分组
     *
     * @return
     */
    String category() default "";

    /**
     * 获取锁最多等待的时间
     *
     * @return
     */
    int timeoutInMills() default 0;

    String failMsg() default "您请求的资源正在操作中，请稍后重试";
}
