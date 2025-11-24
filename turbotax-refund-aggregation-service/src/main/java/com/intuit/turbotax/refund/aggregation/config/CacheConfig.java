package com.intuit.turbotax.refund.aggregation.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for caching in the refund aggregation service.
 * Enables caching and provides cache manager for refund status data.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a cache manager using concurrent maps.
     * Suitable for single-instance applications or development environments.
     *
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("refundStatus");
    }
}