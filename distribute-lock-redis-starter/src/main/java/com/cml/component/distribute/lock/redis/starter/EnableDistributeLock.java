package com.cml.component.distribute.lock.redis.starter;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ImportAutoConfiguration(classes = {RedissonAutoConfiguration.class})
public @interface EnableDistributeLock {
}
