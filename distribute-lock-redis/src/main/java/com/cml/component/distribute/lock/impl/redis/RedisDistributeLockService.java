package com.cml.component.distribute.lock.impl.redis;

import com.cml.component.distribute.lock.core.AbstractDefaultDistributeLockService;
import com.cml.component.distribute.lock.core.DistributeLockListener;
import com.cml.component.distribute.lock.core.LockHolder;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

public class RedisDistributeLockService extends AbstractDefaultDistributeLockService {

    private Redisson redisson;

    public RedisDistributeLockService(DistributeLockListener distributeLockListener, Redisson redisson) {
        super(distributeLockListener);
        this.redisson = redisson;
    }

    protected void tryUnlock(LockHolder lockHolder) {
        if (lockHolder.getLock() instanceof RLock) {
            ((RLock) lockHolder.getLock()).unlock();
        }
    }

    protected LockHolder lock(String category, String key, int timeoutInSecond) {
        RLock lock = redisson.getLock(category + key);
        try {
            boolean lockResult = lock.tryLock(timeoutInSecond, -1, TimeUnit.SECONDS);
            return new LockHolder(lock, lockResult, key, category);
        } catch (InterruptedException e) {
            return new LockHolder(e, false, key, category, e);
        }
    }
}
