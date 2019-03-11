package com.cml.component.distribute.lock.starter;

import com.cml.component.distribute.lock.core.DistributeLockListener;
import com.cml.component.distribute.lock.core.DistributeLockService;
import com.cml.component.distribute.lock.core.key.KeyGenerator;
import com.cml.component.distribute.lock.impl.redis.RedisDistributeLockService;
import com.cml.component.distribute.lock.impl.redis.RedisKeyGenerator;
import com.cml.component.distribute.lock.impl.zk.ZKDistributeLockService;
import com.cml.component.distribute.lock.impl.zk.ZKKeyGenerator;
import com.cml.component.distribute.lock.spring.DistributeLockAspect;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@ConditionalOnProperty(name = "distribute.lock.enable", havingValue = "true")
@Configuration
public class DistributeLockAutoConfiguration {


    @Configuration
    @ConditionalOnProperty(name = "distribute.lock.type", havingValue = "zk")
    public static class ZKDistributeLockConfigutaion {

        @ConditionalOnMissingBean(CuratorFramework.class)
        @Bean
        @ConfigurationProperties(prefix = "distribute.lock.zk")
        public ZKProperties zkProperties() {
            return new ZKProperties();
        }

        @Bean
        @ConditionalOnMissingBean(CuratorFramework.class)
        public CuratorFramework zkCuratorFramework(ZKProperties zkProperties) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkProperties.getRetryInterval(), zkProperties.getRetryCount());
            return CuratorFrameworkFactory.builder().retryPolicy(retryPolicy)
                    .connectString(zkProperties.getConnectUrl())
                    .connectionTimeoutMs(zkProperties.getConnTimeout())
                    .build();
        }

        @Bean
        @ConditionalOnMissingBean(name = "zkKeyGenerator")
        public KeyGenerator zkKeyGenerator() {
            return new ZKKeyGenerator();
        }

        @Bean
        public DistributeLockService zkDistributeLockService(@Autowired(required = false) DistributeLockListener distributeLockListener,
                                                             @Qualifier("zkKeyGenerator") KeyGenerator zkKeyGenerator,
                                                             CuratorFramework curatorFramework) {

            return new ZKDistributeLockService(distributeLockListener, zkKeyGenerator, curatorFramework);
        }

    }

    @Configuration
    @ConditionalOnProperty(name = "distribute.lock.type", havingValue = "redis")
    public static class RedisDistributeLockConfigutaion {

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

        @Bean
        @ConditionalOnMissingBean(name = "redisKeyGenerator")
        public KeyGenerator redisKeyGenerator() {
            return new RedisKeyGenerator();
        }

        @Bean
        public DistributeLockService redisDistributeLockService(@Autowired(required = false) DistributeLockListener distributeLockListener,
                                                                @Qualifier("redisKeyGenerator") KeyGenerator redisKeyGenerator,
                                                                Redisson redisson) {
            return new RedisDistributeLockService(distributeLockListener, redisson, redisKeyGenerator);
        }

    }

    @Bean
    public DistributeLockAspect distributeLockAspect(DistributeLockService distributeLockService) {
        return new DistributeLockAspect(distributeLockService);
    }
}
