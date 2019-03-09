package com.cml.component.distribute.lock.redis.starter;

import com.cml.component.distribute.lock.core.DistributeLockListener;
import com.cml.component.distribute.lock.core.DistributeLockService;
import com.cml.component.distribute.lock.impl.redis.RedisDistributeLockService;
import org.redisson.Redisson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(name = "distribute.lock.redis.enable", havingValue = "true")
public class DistributeLockAutoConfiguration {

    @ConditionalOnBean({DistributeLockListener.class, Redisson.class})
    @Bean
    public DistributeLockService redisDistributeLockService(DistributeLockListener distributeLockListener, Redisson redisson) {
        return new RedisDistributeLockService(distributeLockListener, redisson);
    }

    @ConditionalOnBean({Redisson.class})
    @Bean
    public DistributeLockService redisDistributeLockService(Redisson redisson) {
        return new RedisDistributeLockService(null, redisson);
    }
}
