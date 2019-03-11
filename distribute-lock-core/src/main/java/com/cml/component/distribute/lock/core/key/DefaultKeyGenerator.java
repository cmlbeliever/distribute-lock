package com.cml.component.distribute.lock.core.key;

public class DefaultKeyGenerator implements KeyGenerator {
    public String generate(String category, String key) {
        return category + key;
    }
}
