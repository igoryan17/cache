package com.igoryan.cache.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Configuration {
    protected int sizeOfFirstLevel;
    protected int sizeOfSecondLevel;
}
