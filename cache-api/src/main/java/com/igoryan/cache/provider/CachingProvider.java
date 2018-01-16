package com.igoryan.cache.provider;

import com.igoryan.cache.manager.CacheManager;

import java.io.Closeable;

public interface CachingProvider extends Closeable {
    CacheManager getCacheManager();
}
