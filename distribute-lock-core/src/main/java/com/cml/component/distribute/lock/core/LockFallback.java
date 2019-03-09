package com.cml.component.distribute.lock.core;

/**
 * 获取分布式锁失败
 */
public interface LockFallback {
    Object onLockFail(String category, String key);
}
