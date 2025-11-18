package com.intuit.turbotax.api.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class InMemoryCache<T> implements Cache<T> {
    private final Map<String, T> cache = new ConcurrentHashMap<>();

    @Override
    public Optional<T> get(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public void put(String key, T value) {
        if (key == null || value == null) {
            return;
        }
        cache.put(key, value);
    }
}