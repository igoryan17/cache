package com.igoryan.cache.manager;

import com.igoryan.cache.Cache;
import com.igoryan.cache.configuration.Configuration;

import java.io.Closeable;

public interface CacheManager extends Closeable {
    <K, V, C extends Configuration> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException;

    <K, V> Cache getCache(String cacheName);

    Iterable<String> getCacheNames();

    void destroyCache(String cacheName);

    void close();

    boolean isClosed();
}
