package com.igoryan.cache.services;

import com.igoryan.cache.constants.Directories;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TestFileSystemStorageUtils {
    @Test
    public void testInit() {
        Path headDir = Paths.get(Directories.HEAD);
        FileSystemUtils.deleteRecursively(headDir.toFile());
        FileSystemStorageUtils.init();
        assertTrue(headDir.toFile().exists());
    }

    @Test
    public void testClose() throws IOException {
        Path headDir = Paths.get(Directories.HEAD);
        if (!headDir.toFile().exists()) {
            Files.createDirectory(headDir);
        }
        FileSystemStorageUtils.close();
        assertFalse(headDir.toFile().exists());
    }

    @Test
    public void testCreateCacheDir() {
        FileSystemStorageUtils.init();
        FileSystemStorageUtils.initCacheDirectory("testCache");
        Path cacheDir = Paths.get(Directories.HEAD + "/" + "testCache");
        assertTrue(cacheDir.toFile().exists());
        FileSystemStorageUtils.close();
    }

    @Test
    public void testClearCache() {
        FileSystemStorageUtils.init();
        FileSystemStorageUtils.initCacheDirectory("testClearCache");
        Path cacheDir = Paths.get(Directories.HEAD + "/" + "testClearCache");
        FileSystemStorageUtils.clear("testClearCache");
        assertTrue(cacheDir.toFile().list().length == 0);
        FileSystemStorageUtils.close();
    }

    @Test
    public void testPutAndGet() {
        String value = new String();
        String key = "";
        Long time = System.currentTimeMillis();
        FileSystemStorageUtils.init();
        FileSystemStorageUtils.initCacheDirectory("testPutAndGet");
        FileSystemStorageUtils.put("testPutAndGet", key, value, time);
        Object out = FileSystemStorageUtils.get("testPutAndGet", key, time);
        assertEquals(value, out);
        FileSystemStorageUtils.close();
    }

    @Test
    public void testRemove() {
        String key = "";
        Long time = System.currentTimeMillis();
        String input = new String();
        FileSystemStorageUtils.init();
        FileSystemStorageUtils.initCacheDirectory("testRemove");
        FileSystemStorageUtils.put("testRemove", key, input, time);
        FileSystemStorageUtils.remove("testRemove", key, time);
        Path objectDir = Paths.get(FileSystemStorageUtils.getFilePath("testRemove", key, time));
        assertFalse(objectDir.toFile().exists());
        FileSystemStorageUtils.close();
    }
}
