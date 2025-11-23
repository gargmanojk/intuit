package com.intuit.turbotax.refund.query.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.intuit.turbotax.api.v1.common.service.Cache;

/**
 * Simple in-memory cache implementation with TTL support.
 * Provides thread-safe caching with automatic expiration of entries.
 * 
 * @param <T> the type of values stored in the cache
 */
public class InMemoryCache<T> implements Cache<T> {

    private final ConcurrentMap<String, CacheEntry<T>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;
    private final ScheduledExecutorService cleanupExecutor;

    /**
     * Creates a new cache with the specified TTL.
     * 
     * @param ttlMillis time-to-live for cache entries in milliseconds
     */
    public InMemoryCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
        this.cleanupExecutor = Executors.newScheduledThreadPool(1);

        // Schedule periodic cleanup of expired entries
        cleanupExecutor.scheduleAtFixedRate(this::cleanup, 30, 30, TimeUnit.MINUTES);
    }

    @Override
    public Optional<T> get(String key) {
        CacheEntry<T> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        return Optional.of(entry.getValue());
    }

    @Override
    public void put(String key, T value) {
        if (key == null || value == null) {
            return;
        }
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis));
    }

    /**
     * Returns the current size of the cache.
     * 
     * @return number of entries in the cache
     */
    public int size() {
        return cache.size();
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Removes expired entries from cache.
     * Called automatically by the cleanup scheduler.
     */
    private void cleanup() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Shuts down the cleanup executor.
     * Should be called when the cache is no longer needed.
     */
    public void shutdown() {
        cleanupExecutor.shutdown();
    }

    /**
     * Cache entry with expiration timestamp.
     */
    private static class CacheEntry<T> {
        private final T value;
        private final long expiryTime;

        public CacheEntry(T value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public T getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}