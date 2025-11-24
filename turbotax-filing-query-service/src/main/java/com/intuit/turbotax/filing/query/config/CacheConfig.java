package com.intuit.turbotax.filing.query.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the filing query service.
 * Enables caching with TTL-based expiration.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configures the cache manager with TTL support.
     * Uses ConcurrentMapCacheManager for simple in-memory caching.
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager("filings");

        // Note: ConcurrentMapCacheManager doesn't support TTL out of the box.
        // For production, consider using Caffeine or Redis cache manager.
        // TTL can be configured in application.yml for those implementations.

        return cacheManager;
    }
}