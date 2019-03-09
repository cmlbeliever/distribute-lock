package com.cml.component.distribute.lock.core;

/**
 * @Auther: cml
 * @Date: 2018-11-15 14:57
 * @Description:
 */
public class LockHolder {
    private Object lock;
    private boolean lockSuccess;
    private String key;
    private String category;

    public LockHolder(Object lock, boolean lockSuccess, String key, String category) {
        this.lock = lock;
        this.lockSuccess = lockSuccess;
        this.key = key;
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Object getLock() {
        return lock;
    }

    public boolean isLockSuccess() {
        return lockSuccess;
    }

    public boolean shouldUnLock() {
        return isLockSuccess() && lock != null;
    }
}
