package com.intuit.turbotax.api.service;

import java.util.Optional;

public interface Cache<T> {
    Optional<T> get(String key);
    void put(String key, T value);
}