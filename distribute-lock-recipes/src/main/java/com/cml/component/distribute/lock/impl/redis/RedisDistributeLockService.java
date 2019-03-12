package com.cml.component.distribute.lock.impl.redis;

import com.cml.component.distribute.lock.core.AbstractDefaultDistributeLockService;
import com.cml.component.distribute.lock.core.DistributeLockListener;
import com.cml.component.distribute.lock.core.LockHolder;
import com.cml.component.distribute.lock.core.key.KeyGenerator;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

public class RedisDistributeLockService extends AbstractDefaultDistributeLockService {

    private Redisson redisson;

    public RedisDistributeLockService(DistributeLockListener distributeLockListener, Redisson redisson, KeyGenerator keyGenerator) {
        super(distributeLockListener, keyGenerator);
        this.redisson = redisson;
    }

    protected void tryUnlock(LockHolder lockHolder) {
        if (lockHolder.getLock() instanceof RLock) {
            ((RLock) lockHolder.getLock()).unlock();
        }
    }

    protected LockHolder lock(String category, String key, int timeoutInMills) {
        RLock lock = redisson.getLock(super.generateKey(category, key));
        try {
            boolean lockResult = lock.tryLock(timeoutInMills, -1, TimeUnit.MILLISECONDS);
            return new LockHolder(lock, lockResult, key, category);
        } catch (InterruptedException e) {
            return new LockHolder(e, false, key, category, e);
        }
    }
}
