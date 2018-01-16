package com.igoryan.cache.services;

import com.igoryan.cache.CacheException;
import com.igoryan.cache.constants.Directories;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemStorageUtils {

    private FileSystemStorageUtils() {
    }

    public static void init() {
        Path head = Paths.get(Directories.HEAD);
        if (!head.toFile().exists()) {
            try {
                Files.createDirectory(head);
            } catch (IOException e) {
                throw new CacheException(e);
            }
        }
    }

    public static void initCacheDirectory(String cacheName) {
        Path cacheDir = Paths.get(Directories.HEAD + "/" + cacheName);
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

    public static void put(String cacheName, Object key, Object value, Long time) {
        Path fileOfObject = Paths.get(getFilePath(cacheName, key, time));
        try (
            FileOutputStream fileOutputStream = new FileOutputStream(fileOfObject.toFile());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        ) {
            objectOutputStream.writeObject(value);
            objectOutputStream.close();
        }
        catch (IOException e) {
            throw new CacheException(e);
        }
    }

    public static boolean cached(String cacheName, Object key, Long time) {
        Path objectDir = Paths.get(getFilePath(cacheName, key, time));
        return objectDir.toFile().exists();
    }

    public static Object get(String cacheName, Object key, Long time) {
        Path fileObject = Paths.get(getFilePath(cacheName, key, time));
        try (
                FileInputStream fileInputStream = new FileInputStream(fileObject.toFile());
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                ) {
            Object result = objectInputStream.readObject();
            objectInputStream.close();
            return result;
        }
        catch (IOException | ClassNotFoundException e) {
            throw new CacheException(e);
        }
    }

    public static void remove(String cacheName, Object key, Long time) {
        Path fileObject = Paths.get(getFilePath(cacheName, key, time));
        try {
            Files.delete(fileObject);
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

    public static void clear(String cacheName) {
        Path cacheDir = Paths.get(Directories.HEAD + "/" + cacheName);
        for (File objectFile : cacheDir.toFile().listFiles()) {
            objectFile.delete();
        }
    }

    public static void closeCache(String cacheName) {
        FileSystemUtils.deleteRecursively(Paths.get(Directories.HEAD + "/" + cacheName).toFile());
    }

    public static String getFilePath(String cacheName, Object key, Long time) {
        return Directories.HEAD + "/" + cacheName +
                "/" + time +
                "_" + String.valueOf(key.hashCode());
    }

    public static void close() {
        Path headDir = Paths.get(Directories.HEAD);
        if (headDir.toFile().exists()) {
            FileSystemUtils.deleteRecursively(headDir.toFile());
        }
    }
}
