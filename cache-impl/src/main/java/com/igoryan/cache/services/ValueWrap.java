package com.igoryan.cache.services;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValueWrap<V> {
    private final V value;
    private final long time;
}
