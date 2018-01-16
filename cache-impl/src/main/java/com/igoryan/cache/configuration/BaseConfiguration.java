package com.igoryan.cache.configuration;

import com.igoryan.cache.constants.Strategy;
import lombok.Getter;

@Getter
public class BaseConfiguration extends Configuration {
    private Strategy cacheStrategy;

    public BaseConfiguration(int sizeOfFirstLevel, int sizeOfSecondLevel, Strategy cacheStrategy) {
        super(sizeOfFirstLevel, sizeOfSecondLevel);
        this.cacheStrategy = cacheStrategy;
    }
}
