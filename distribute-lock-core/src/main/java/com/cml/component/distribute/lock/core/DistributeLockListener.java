package com.cml.component.distribute.lock.core;

public interface DistributeLockListener {
    void beforeLock(String category, String key);

    void onLockSuccess(String category, String key);

    void onLockFail(String category, String key, Exception e);

    void onUnlock(String category, String key);
}
