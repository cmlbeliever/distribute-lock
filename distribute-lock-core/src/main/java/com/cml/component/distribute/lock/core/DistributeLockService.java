package com.cml.component.distribute.lock.core;

/**
 * @Auther: cml
 * @Date: 2018-11-22 10:11
 * @Description:
 */
public interface DistributeLockService {
    /**
     * @param category
     * @param key
     * @param timeoutInMills
     * @return never be null
     */
    LockHolder getLock(String category, String key, int timeoutInMills);

    void unLock(LockHolder lockHolder);
}
