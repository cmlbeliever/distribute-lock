package com.cml.component.distribute.lock.redis.sample;

import com.cml.component.distribute.lock.core.DistributeLockListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LockListener implements DistributeLockListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeLock(String category, String key) {
        logger.info("<<<beforeLock>>>,category:{},key:{}", category, key);
    }

    @Override
    public void onLockSuccess(String category, String key) {
        logger.info("<<<onLockSuccess>>>,category:{},key:{}", category, key);
    }

    @Override
    public void onLockFail(String category, String key, Exception e) {
        logger.info("<<<onLockFail>>>,category:{},key:{}，ex:{}", category, key, e);
    }

    @Override
    public void onUnlock(String category, String key) {
        logger.info("<<<onUnlock>>>,category:{},key:{}", category, key);
    }
}
