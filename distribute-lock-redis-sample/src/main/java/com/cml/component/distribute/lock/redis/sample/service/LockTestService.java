package com.cml.component.distribute.lock.redis.sample.service;

import com.cml.component.distribute.lock.core.DistributeLock;
import org.springframework.stereotype.Service;

@Service
public class LockTestService {

    @DistributeLock(category = "lockService", key = "#arg0")
    public String testLock(String key) {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "getLockSuccess";
    }

    @DistributeLock(category = "lockService", key = "#arg0", timeoutInSeconds = 3)
    public String testReentrantLock(String key) {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "getLockSuccess";
    }
}
