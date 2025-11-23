package com.intuit.turbotax.api.v1.common.service;

import java.util.Optional;

/**
 * Generic cache interface for storing and retrieving values by key.
 * Provides methods to get and put values in the cache.
 *
 * @param <T> the type of value to cache
 */
public interface Cache<T> {

    /**
     * Retrieves a value from the cache by key.
     *
     * @param key the cache key
     * @return an Optional containing the value if present, or empty if not found
     */
    Optional<T> get(String key);

    /**
     * Puts a value into the cache with the specified key.
     *
     * @param key   the cache key
     * @param value the value to cache
     */
    void put(String key, T value);
}