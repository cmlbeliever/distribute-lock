package com.cml.component.distribute.lock.core;

public abstract class AbstractDefaultDistributeLockService implements DistributeLockService {

    private DistributeLockListener distributeLockListener;

    public AbstractDefaultDistributeLockService(DistributeLockListener distributeLockListener) {
        this.distributeLockListener = distributeLockListener;
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

    /**
     * @param category
     * @param key
     * @param timeoutInSecond
     * @return never be null
     */
    protected abstract LockHolder lock(String category, String key, int timeoutInSecond);


}
