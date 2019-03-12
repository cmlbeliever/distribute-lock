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
     * 获取锁最多等待的时间,0表示直接获取，获取失败不等待。如：1000，表示每次最多会等待1s，如果1s内锁被释放了则会被获取到
     *
     * @return
     */
    int maxWaitTimeInMills() default 0;

    String failMsg() default "您请求的资源正在操作中，请稍后重试";
}
