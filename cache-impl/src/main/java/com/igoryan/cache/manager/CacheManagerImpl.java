package com.igoryan.cache.manager;

import com.igoryan.cache.BaseCache;
import com.igoryan.cache.Cache;
import com.igoryan.cache.configuration.BaseConfiguration;
import com.igoryan.cache.configuration.Configuration;
import com.igoryan.cache.factory.CacheFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class CacheManagerImpl implements CacheManager {
    private final ConcurrentMap<String, BaseCache> caches = new ConcurrentHashMap<>();
    private final AtomicBoolean closed = new AtomicBoolean(false);

    @Override
    public <K, V, C extends Configuration> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
        BaseCache<K, V> result = null;
        if (configuration instanceof BaseConfiguration) {
            switch (((BaseConfiguration) configuration).getCacheStrategy()) {
                case LRU:
                    result = CacheFactory.createLruCache(configuration, cacheName);
                    break;
            }
        } else {
            result = CacheFactory.createLruCache(configuration, cacheName);
        }
        caches.put(cacheName, result);
        result.init();
        return result;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return caches.get(cacheName);
    }

    @Override
    public Iterable<String> getCacheNames() {
        return caches.keySet();
    }

    @Override
    public void destroyCache(String cacheName) {
        BaseCache cache = caches.remove(cacheName);
        cache.close();
    }

    @Override
    public void close() {
        synchronized (caches) {
            caches.values().forEach(BaseCache::close);
            caches.clear();
        }
        closed.set(true);
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }
}
