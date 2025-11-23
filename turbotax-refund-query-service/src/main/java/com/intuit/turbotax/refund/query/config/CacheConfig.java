package com.intuit.turbotax.refund.query.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.intuit.turbotax.api.v1.common.service.Cache;
import com.intuit.turbotax.api.v1.refund.model.RefundSummary;
import com.intuit.turbotax.refund.query.cache.InMemoryCache;

/**
 * Cache configuration for RefundQueryService.
 * Provides TTL-based caching for refund summary results.
 */
@Configuration
public class CacheConfig {

    /**
     * Creates a cache for RefundSummary lists with 10-minute TTL.
     * Used to cache orchestrated refund summary results per user ID.
     * 
     * @return Cache instance for RefundSummary lists
     */
    @Bean
    public Cache<List<RefundSummary>> refundSummaryCache() {
        return new InMemoryCache<>(30 * 60 * 1000L); // 30 minutes TTL
    }
}