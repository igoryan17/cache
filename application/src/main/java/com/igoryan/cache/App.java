package com.igoryan.cache;

import com.igoryan.cache.configuration.BaseConfiguration;
import com.igoryan.cache.constants.Strategy;
import com.igoryan.cache.manager.CacheManager;
import com.igoryan.cache.provider.CachingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    @Autowired
    public CommandLineRunner commandLineRunner(CachingProvider cachingProvider) {
        return args -> {
            CacheManager cacheManager = cachingProvider.getCacheManager();
            BaseConfiguration configuration = new BaseConfiguration(4, 8, Strategy.LRU);
            Cache<String, String> firstCache = cacheManager.createCache("first", configuration);
            Cache<String, String> secondCache = cacheManager.createCache("second", configuration);
            final int objectsCount = 10;
            for (int i = 0; i < objectsCount; i++) {
                String key = String.valueOf(i);
                Thread.sleep(10);
                String value = "in first cache object number " + key;
                firstCache.put(key, value);
                LOGGER.debug("key = {}, value = {} in first cache", key, value);
                secondCache.put(key, value);
                LOGGER.debug("key = {}, value = {} in second cache", key, value);
            }
            for (int i = 0; i < objectsCount; i++) {
                String key = String.valueOf(i);
                Thread.sleep(10);
                LOGGER.debug("value = {} retrieved from first cache for key = {}", firstCache.get(key), key);
                LOGGER.debug("value = {} retrieved from second cache for key = {}", firstCache.get(key), key);
            }
            cacheManager.close();
            cachingProvider.close();
        };
    }
}
