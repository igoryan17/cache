package com.igoryan.cache;

import com.igoryan.cache.services.FileSystemStorageUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TestLruCache {
    private LruCache<String, String> cache = new LruCache<>(8, 16, "testLruCache");

    @Before
    public void init() {
        FileSystemStorageUtils.init();
        cache.init();
    }

    @Test
    public void testPutAndGet() {
        String input = new String();
        cache.put("putAndGet", input);
        assertEquals(input, cache.get("putAndGet"));
    }

    @Test
    public void testOverload() {
        HashMap<String, String> objects = new HashMap<>();
        int size = 14;
        for (int i = 0; i < size; i++) {
            String keyAndValue = String.valueOf(i);
            objects.put(keyAndValue, keyAndValue);
        }
        objects.forEach((key, value) -> cache.put(key, value));
        objects.forEach((key, value) -> {
            assertEquals(value, cache.get(key));
        });
    }

    @After
    public void close() {
        cache.close();
        FileSystemStorageUtils.close();
    }
}
