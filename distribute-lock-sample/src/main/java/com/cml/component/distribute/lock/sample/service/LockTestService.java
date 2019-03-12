package com.cml.component.distribute.lock.sample.service;

import com.cml.component.distribute.lock.core.DistributeLock;
import com.cml.component.distribute.lock.core.DistributeLockHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LockTestService {

    @Autowired
    private DistributeLockHelper distributeLockHelper;

    @DistributeLock(category = "lockService", key = "#arg0")
    public String testLock(String key) {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "getLockSuccess";
    }

    public String testLock2(String key) {
        return distributeLockHelper.tryLock("lockService", key, 0, () -> "getLockSuccess");
    }
}
