package com.cml.component.distribute.lock.impl.zk;

import com.cml.component.distribute.lock.core.AbstractDefaultDistributeLockService;
import com.cml.component.distribute.lock.core.DistributeLockListener;
import com.cml.component.distribute.lock.core.LockHolder;
import com.cml.component.distribute.lock.core.key.KeyGenerator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

public class ZKDistributeLockService extends AbstractDefaultDistributeLockService {

    private CuratorFramework curatorFramework;

    public ZKDistributeLockService(DistributeLockListener distributeLockListener, KeyGenerator keyGenerator, CuratorFramework curatorFramework) {
        super(distributeLockListener, keyGenerator);
        this.curatorFramework = curatorFramework;
    }

    protected void tryUnlock(LockHolder lockHolder) throws Exception {
        if (lockHolder.getLock() instanceof InterProcessMutex) {
            ((InterProcessMutex) lockHolder.getLock()).release();
        }
    }

    protected LockHolder lock(String category, String key, int timeoutInMills) {
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, generateKey(category, key));

        try {
            if (lock.acquire(timeoutInMills, TimeUnit.MILLISECONDS)) {
                return new LockHolder(lock, true, key, category);
            }
            return new LockHolder(null, false, key, category);
        } catch (Exception e) {
            return new LockHolder(e, false, key, category, e);
        }
    }
}
