package com.cml.component.distribute.lock.impl.zk;

import com.cml.component.distribute.lock.core.key.KeyGenerator;

public class ZKKeyGenerator implements KeyGenerator {
    public String generate(String category, String key) {
        return (category.startsWith("/") ? category : "/" + category) + "/" + key;
    }
}
