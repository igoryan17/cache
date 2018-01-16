package com.igoryan.cache.factory;

import com.igoryan.cache.BaseCache;
import com.igoryan.cache.LruCache;
import com.igoryan.cache.configuration.Configuration;

public class CacheFactory {
    public static <K, V> BaseCache<K, V> createLruCache(Configuration configuration, String cacheName) {
        return new LruCache<>(configuration.getSizeOfFirstLevel(), configuration.getSizeOfSecondLevel(), cacheName);
    }
}
