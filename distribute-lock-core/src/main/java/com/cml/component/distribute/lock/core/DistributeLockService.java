package com.cml.component.distribute.lock.core;

/**
 * @Auther: cml
 * @Date: 2018-11-22 10:11
 * @Description:
 */
public interface DistributeLockService {
    LockHolder getLock(String category, String key, int timeoutInSecond);

    void unLock(LockHolder lockHolder);
}
