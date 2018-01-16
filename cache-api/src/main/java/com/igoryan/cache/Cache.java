package com.igoryan.cache;

import java.util.Map;
import java.util.Set;

public interface Cache<K, V> {
    V get(K key);

    Map<K, V> getAll(Set<K> keys);

    boolean containsKey(K key);

    void put(K key, V value);

    boolean putIfAbsent(K key, V value);

    boolean remove(K key);

    V getAndRemove(K key);

    void clear();
}
