package com.igoryan.cache.provider;

import com.igoryan.cache.manager.CacheManager;
import com.igoryan.cache.manager.CacheManagerImpl;
import com.igoryan.cache.services.FileSystemStorageUtils;

public class CachingProviderImpl implements CachingProvider {
    @Override
    public CacheManager getCacheManager() {
        return new CacheManagerImpl();
    }

    @Override
    public void close() {
        FileSystemStorageUtils.close();
    }
}
