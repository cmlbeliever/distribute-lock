package com.cml.component.distribute.lock.core;

import com.cml.component.distribute.lock.core.key.DefaultKeyGenerator;
import com.cml.component.distribute.lock.core.key.KeyGenerator;

import java.util.Optional;

public abstract class AbstractDefaultDistributeLockService implements DistributeLockService {

    private DistributeLockListener distributeLockListener;
    private KeyGenerator keyGenerator;

    public AbstractDefaultDistributeLockService(DistributeLockListener distributeLockListener, KeyGenerator keyGenerator) {
        this.distributeLockListener = distributeLockListener;
        this.keyGenerator = Optional.ofNullable(keyGenerator).orElse(new DefaultKeyGenerator());
    }

    public LockHolder getLock(String category, String key, int timeoutInSecond) {
        LockHolder lockHolder = null;
        try {
            beforeLock(category, key);
            lockHolder = lock(category, key, timeoutInSecond);
        } catch (Exception e) {
            lockHolder = new LockHolder(null, false, key, category, e);
        } finally {
            notifyAfterLock(category, key, lockHolder);
        }
        return lockHolder;
    }

    private void notifyAfterLock(String category, String key, LockHolder lockHolder) {
        if (lockHolder.isLockSuccess()) {
            onLockSuccess(category, key);
        } else {
            onLockFail(category, key, lockHolder.getException());
        }
    }

    public void unLock(LockHolder lockHolder) {
        if (lockHolder.shouldUnLock()) {
            try {
                tryUnlock(lockHolder);
                notifyUnlock(lockHolder.getCategory(), lockHolder.getKey(), null);
            } catch (Exception e) {
                notifyUnlock(lockHolder.getCategory(), lockHolder.getKey(), e);
            }
        }
    }

    private void notifyUnlock(String category, String key, Exception e) {
        if (null != distributeLockListener) {
            distributeLockListener.onUnlock(category, key, e);
        }
    }

    protected abstract void tryUnlock(LockHolder lockHolder) throws Exception;

    private void onLockFail(String category, String key, Exception exception) {
        if (null != distributeLockListener) {
            distributeLockListener.onLockFail(category, key, exception);
        }
    }

    private void onLockSuccess(String category, String key) {
        if (null != distributeLockListener) {
            distributeLockListener.onLockSuccess(category, key);
        }
    }

    protected void beforeLock(String category, String key) {
        if (null != distributeLockListener) {
            distributeLockListener.beforeLock(category, key);
        }
    }

    protected String generateKey(String category, String key) {
        return keyGenerator.generate(category, key);
    }

    /**
     * @param category
     * @param key
     * @param timeoutInSecond
     * @return never be null
     */
    protected abstract LockHolder lock(String category, String key, int timeoutInSecond);


}
