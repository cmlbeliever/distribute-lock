package com.cml.component.distribute.lock.sample;

import com.cml.component.distribute.lock.core.DistributeLockListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LockListener implements DistributeLockListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeLock(String category, String key) {
        logger.info("Thread:{},<<<beforeLock>>>,category:{},key:{}", Thread.currentThread().getId(), category, key);
    }

    @Override
    public void onLockSuccess(String category, String key) {
        logger.info("Thread:{},<<<onLockSuccess>>>,category:{},key:{}", Thread.currentThread().getId(), category, key);
    }

    @Override
    public void onLockFail(String category, String key, Exception e) {
        logger.info("Thread:{},<<<onLockFail>>>,category:{},key:{}，ex:{}", Thread.currentThread().getId(), category, key, e);
    }

    @Override
    public void onUnlock(String category, String key, Exception e) {
        logger.info("Thread:{},<<<onUnlock>>>,category:{},key:{},ex:{}", Thread.currentThread().getId(), category, key, e);
    }
}
