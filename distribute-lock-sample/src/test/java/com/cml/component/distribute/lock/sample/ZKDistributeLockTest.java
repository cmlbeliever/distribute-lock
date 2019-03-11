package com.cml.component.distribute.lock.sample;


import com.cml.component.distribute.lock.core.DistributeLockService;
import com.cml.component.distribute.lock.sample.service.LockTestService;
import com.cml.component.distribute.lock.starter.DistributeLockAutoConfiguration;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"spring.config.location = classpath:application-zk.yaml"})
@EnableConfigurationProperties
@EnableAspectJAutoProxy
@ContextConfiguration(classes = {LockTestService.class,
        LockListener.class,
        PropertyPlaceholderAutoConfiguration.class,
        PropertySourcesPlaceholderConfigurer.class,
        ConfigurationPropertiesBindingPostProcessorRegistrar.class,
        DistributeLockAutoConfiguration.class}, initializers = ConfigFileApplicationContextInitializer.class)
public class ZKDistributeLockTest {

    @Autowired
    private LockTestService lockTestService;

    @Autowired
    private CuratorFramework curatorFramework;
    @Autowired
    private DistributeLockService distributeLockService;
    @Autowired
    private LockListener lockListener;

    /**
     * 可重入锁测试
     */
    @Test
    public void testReentrantLock() throws Exception {
        String key = "testKeyZK";

        String result = lockTestService.testLock(key);

        assert "getLockSuccess".equals(result);

        result = lockTestService.testReentrantLock(key);
        assert "getLockSuccess".equals(result);

        Thread.sleep(1000);
    }

    @Test
    public void testReentrantLockException() throws InterruptedException {
        String key = "testKeyZK";

        int sampleCount = 3;
        AtomicInteger successCounter = new AtomicInteger();
        AtomicInteger failCounter = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(sampleCount);

        CountDownLatch countDownLatch = new CountDownLatch(sampleCount);

        for (int i = 0; i < sampleCount; i++) {
            executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    countDownLatch.await();
                    String result = lockTestService.testLock(key);
                    lockTestService.testLock(key);
                    lockTestService.testLock(key);
                    lockTestService.testLock(key);
                    successCounter.incrementAndGet();
                } catch (Throwable e) {
                    e.printStackTrace();
                    failCounter.incrementAndGet();
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

        assert successCounter.get() == 1;
        assert failCounter.get() == sampleCount - 1;
    }

}
