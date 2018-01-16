package com.igoryan.cache;

import com.igoryan.cache.services.FileSystemStorageUtils;
import com.igoryan.cache.services.ValueWrap;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class LruCache<K, V> implements BaseCache<K, V> {
    private final ConcurrentMap<K, ValueWrap<V>> firstLevelStorage = new ConcurrentHashMap<>();
    private final ConcurrentMap<K, Long> secondLevelTimeTrackingMap = new ConcurrentHashMap<>();
    private final int sizeOfFirstLevel;
    private final int sizeOfSecondLevel;
    private final String cacheName;
    private static final int SLEEPING_MILLISECONDS = 1000;
    private final Thread collector;

    public LruCache(int sizeOfFirstLevel, int sizeOfSecondLevel, String cacheName) {
        this.sizeOfFirstLevel = sizeOfFirstLevel;
        this.sizeOfSecondLevel = sizeOfSecondLevel;
        this.cacheName = cacheName;
        collector = initCollector();
    }

    private Thread initCollector() {
        return new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(SLEEPING_MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (firstLevelStorage.size() > sizeOfFirstLevel) {
                    synchronized (firstLevelStorage) {
                        firstLevelStorage.entrySet().stream()
                                .sorted(Comparator.comparingLong(valueWrapEntry -> valueWrapEntry.getValue().getTime()))
                                .limit((sizeOfFirstLevel / 3) + 1)
                                .forEachOrdered(entry -> {
                                    secondLevelTimeTrackingMap.put(entry.getKey(), entry.getValue().getTime());
                                    FileSystemStorageUtils.put(cacheName, entry.getKey(),
                                            entry.getValue().getValue(), entry.getValue().getTime());
                                });

                    }
                }
                if (secondLevelTimeTrackingMap.size() > sizeOfSecondLevel) {
                    synchronized (secondLevelTimeTrackingMap) {
                        secondLevelTimeTrackingMap.entrySet().stream()
                                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                                .limit(sizeOfSecondLevel / 4 + 1)
                                .forEachOrdered(entry -> {
                                    FileSystemStorageUtils.remove(cacheName, entry.getKey(), entry.getValue());
                                });
                    }
                }
            }
        });
    }

    @Override
    public V get(K key) {
        Optional<ValueWrap<V>> fromFirstLevel = Optional.ofNullable((firstLevelStorage.get(key)));
        if (fromFirstLevel.isPresent()) {
            return fromFirstLevel.get().getValue();
        }
        Optional<Long> fromSecondLevel = Optional.ofNullable(secondLevelTimeTrackingMap.get(key));
        if (fromSecondLevel.isPresent()) {
            return (V) FileSystemStorageUtils.get(cacheName, key, fromSecondLevel.get());
        }
        return null;
    }

    @Override
    public Map<K, V> getAll(final Set<K> keys) {
        return keys.stream().collect(Collectors.toMap(key -> key, this::get));
    }

    @Override
    public boolean containsKey(K key) {
        return firstLevelStorage.containsKey(key) || secondLevelTimeTrackingMap.containsKey(key);
    }

    @Override
    public void put(K key, V value) {
        firstLevelStorage.put(key, new ValueWrap<>(value, System.currentTimeMillis()));
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        ValueWrap<V> result = firstLevelStorage.putIfAbsent(key, new ValueWrap<>(value, System.currentTimeMillis()));
        return result == null;
    }

    @Override
    public boolean remove(K key) {
        synchronized (firstLevelStorage) {
            if (firstLevelStorage.containsKey(key)) {
                firstLevelStorage.remove(key);
                return true;
            }
        }
        synchronized (secondLevelTimeTrackingMap) {
            if (secondLevelTimeTrackingMap.containsKey(key)) {
                FileSystemStorageUtils.remove(cacheName, key, secondLevelTimeTrackingMap.remove(key));
                return true;
            }
        }
        return false;
    }

    @Override
    public V getAndRemove(K key) {
        synchronized (firstLevelStorage) {
            if (firstLevelStorage.containsKey(key)) {
                return firstLevelStorage.remove(key).getValue();
            }
        }
        synchronized (secondLevelTimeTrackingMap) {
            if (secondLevelTimeTrackingMap.containsKey(key)) {
                Long time = secondLevelTimeTrackingMap.remove(key);
                V result = (V) FileSystemStorageUtils.get(cacheName, key, time);
                FileSystemStorageUtils.remove(cacheName, key, time);
                return result;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        synchronized (firstLevelStorage) {
            firstLevelStorage.clear();
        }
        synchronized (secondLevelTimeTrackingMap) {
            secondLevelTimeTrackingMap.clear();
            FileSystemStorageUtils.clear(cacheName);
        }
    }

    @Override
    public void init() {
        collector.start();
        FileSystemStorageUtils.initCacheDirectory(cacheName);
    }

    @Override
    public void close() {
        collector.interrupt();
        clear();
        FileSystemStorageUtils.closeCache(cacheName);
    }
}
