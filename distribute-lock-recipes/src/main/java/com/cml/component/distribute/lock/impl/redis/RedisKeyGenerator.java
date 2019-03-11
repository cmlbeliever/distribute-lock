package com.cml.component.distribute.lock.impl.redis;

import com.cml.component.distribute.lock.core.key.KeyGenerator;

public class RedisKeyGenerator implements KeyGenerator {
    public String generate(String category, String key) {
        return category + ":" + key;
    }
}
