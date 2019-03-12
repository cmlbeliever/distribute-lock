package com.cml.component.distribute.lock.core;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DistributeLockHelper {
    private DistributeLockService distributeLockService;

    public DistributeLockHelper(DistributeLockService distributeLockService) {
        this.distributeLockService = distributeLockService;
    }

    public <T> T tryLock(String category, String key, int timeoutInMills, Supplier<T> onLockSuccess, Consumer<Exception> onLockFail, Supplier<T> fallback) {
        LockHolder distributeLock = distributeLockService.getLock(category, key, timeoutInMills);
        if (distributeLock.isLockSuccess()) {
            try {
                return onLockSuccess.get();
            } finally {
                distributeLockService.unLock(distributeLock);
            }
        }
        Optional.ofNullable(onLockFail).ifPresent(t -> onLockFail.accept(distributeLock.getException()));
        return null == fallback ? null : fallback.get();
    }

    public <T> T tryLock(String category, String key, int timeoutInMills, Supplier<T> onLockSuccess) {
        return this.tryLock(category, key, timeoutInMills, onLockSuccess, null, null);
    }
}
