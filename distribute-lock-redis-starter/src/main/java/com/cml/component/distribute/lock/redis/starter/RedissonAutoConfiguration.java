package com.cml.component.distribute.lock.redis.starter;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;


@ConditionalOnProperty(name = "distribute.lock.redis.enable", havingValue = "true")
public class RedissonAutoConfiguration {

    @ConditionalOnMissingBean(Redisson.class)
    @Bean
    @ConfigurationProperties(prefix = "distribute.lock.redis")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @ConditionalOnMissingBean(Redisson.class)
    @Bean
    public RedissonClient createRedissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        String type = redisProperties.getType();
        if ("single".equals(type)) {
            config.useSingleServer()
                    .setAddress(redisProperties.getNodes().get(0))
                    .setTimeout(redisProperties.getTimeout())
                    .setPassword(trimToNull(redisProperties.getPassword()))
                    .setRetryAttempts(redisProperties.getRetryCount())
                    .setDatabase(redisProperties.getDataBase())
                    .setRetryInterval(redisProperties.getRetryInterval())
                    .setConnectTimeout(redisProperties.getTimeout());
        } else if ("sentinel".equals(type)) {
            config.useSentinelServers()
                    .addSentinelAddress(redisProperties.getNodes().toArray(new String[]{}))
                    .setTimeout(redisProperties.getTimeout())
                    .setDatabase(redisProperties.getDataBase())
                    .setPassword(trimToNull(redisProperties.getPassword()))
                    .setRetryAttempts(redisProperties.getRetryCount())
                    .setRetryInterval(redisProperties.getRetryInterval())
                    .setMasterName(redisProperties.getMasterName())
                    .setConnectTimeout(redisProperties.getTimeout());
        } else if ("master-slave".equals(type)) {
            config.useMasterSlaveServers()
                    .setDatabase(redisProperties.getDataBase())
                    .addSlaveAddress(redisProperties.getNodes().toArray(new String[]{}))
                    .setTimeout(redisProperties.getTimeout())
                    .setConnectTimeout(redisProperties.getTimeout())
                    .setRetryAttempts(redisProperties.getRetryCount())
                    .setPassword(trimToNull(redisProperties.getPassword()))
                    .setRetryInterval(redisProperties.getRetryInterval());
        }
        return Redisson.create(config);
    }

    private String trimToNull(String str) {
        return StringUtils.isEmpty(str) ? null : str;
    }
}
