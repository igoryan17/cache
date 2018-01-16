package com.igoryan.cache;

public interface BaseCache<K, V> extends Cache<K, V> {
    void init();
    void close();
}
