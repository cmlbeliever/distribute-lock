package com.cml.component.distribute.lock.redis.sample.service;


import com.cml.component.distribute.lock.core.DistributeLockService;
import com.cml.component.distribute.lock.redis.sample.LockListener;
import com.cml.component.distribute.lock.redis.starter.DistributeLockAutoConfiguration;
import com.cml.component.distribute.lock.redis.starter.RedissonAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessorRegistrar;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = {"classpath:application.yaml"})
@EnableConfigurationProperties
@EnableAspectJAutoProxy
@ContextConfiguration(classes = {LockTestService.class,
        PropertyPlaceholderAutoConfiguration.class,
        PropertySourcesPlaceholderConfigurer.class,
        ConfigurationPropertiesBindingPostProcessorRegistrar.class,
        RedissonAutoConfiguration.class,
        DistributeLockAutoConfiguration.class,
        LockListener.class}, initializers = ConfigFileApplicationContextInitializer.class)
public class LockTestServiceTest {

    @Autowired
    private LockTestService lockTestService;

    @Autowired
    private Redisson redisson;
    @Autowired
    private DistributeLockService distributeLockService;
    @Autowired
    private LockListener lockListener;

    /**
     * 可重入锁测试
     */
    @Test
    public void testReentrantLock() {
        String key = "testKey";

        String result = lockTestService.testLock(key);

        assert "getLockSuccess".equals(result);

        result = lockTestService.testReentrantLock(key);
        assert "getLockSuccess".equals(result);
    }

    @Test
    public void testReentrantLockException() throws InterruptedException {
        String key = "testKey";

        int sampleCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(sampleCount);

        CountDownLatch countDownLatch = new CountDownLatch(sampleCount);

        for (int i = 0; i < sampleCount; i++) {
            executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    countDownLatch.await();
                    String result = lockTestService.testLock(key);
                    System.out.println("======================>" + Thread.currentThread().getId() + ":" + result);
                } catch (Throwable e) {
                    System.out.println("======================>" + Thread.currentThread().getId() + ":" + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        System.out.println("--------------execute end-----------");
        executorService.shutdown();

        try {
            while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
