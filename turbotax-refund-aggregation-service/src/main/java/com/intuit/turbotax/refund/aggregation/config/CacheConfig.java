package com.intuit.turbotax.refund.aggregation.config;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.api.service.Cache;
import com.intuit.turbotax.api.service.InMemoryCache;

/**
 * Cache configuration for RefundAggregationService.
 * Provides TTL-based caching for refund status aggregation results.
 */
@Configuration
public class CacheConfig {

    /**
     * Creates a cache for Optional RefundStatusData with 4-hour TTL.
     * Used to cache aggregated refund status results per filing ID.
     * 
     * @return Cache instance for Optional RefundStatusData
     */
    @Bean
    public Cache<Optional<RefundStatusData>> statusCache() {
        return new InMemoryCache<>(4 * 60 * 60 * 1000L); // 4 hours TTL
    }
}