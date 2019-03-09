package com.cml.component.distribute.lock.core;

public abstract class AbstractDefaultDistributeLockService implements DistributeLockService {

    private DistributeLockListener distributeLockListener;

    public AbstractDefaultDistributeLockService(DistributeLockListener distributeLockListener) {
        this.distributeLockListener = distributeLockListener;
    }

    public LockHolder getLock(String category, String key, int timeoutInSecond) {
        LockHolder lockHolder = null;
        Exception exception = null;
        try {
            beforeLock(category, key);
            lockHolder = lock(category, key, timeoutInSecond);
            if (lockHolder != null) {
                onLockSuccess(category, key);
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            if (lockHolder == null) {
                onLockFail(category, key, exception);
            }
        }
        return lockHolder;
    }

    public void unLock(LockHolder lockHolder) {
        if (lockHolder.shouldUnLock()) {
            tryUnlock(lockHolder);
            if (null != distributeLockListener) {
                distributeLockListener.onUnlock(lockHolder.getCategory(), lockHolder.getKey());
            }
        }
    }

    protected abstract void tryUnlock(LockHolder lockHolder);

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

    protected abstract LockHolder lock(String category, String key, int timeoutInSecond);


}
