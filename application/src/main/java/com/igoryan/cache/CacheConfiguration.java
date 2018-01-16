package com.igoryan.cache;

import com.igoryan.cache.provider.CachingProvider;
import com.igoryan.cache.provider.CachingProviderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public CachingProvider cachingProvider() {
        return new CachingProviderImpl();
    }
}
