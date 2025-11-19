package com.intuit.turbotax.refund.prediction.config;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.intuit.turbotax.api.service.Cache;
import com.intuit.turbotax.api.service.InMemoryCache;
import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.refund.prediction.ml.PredictionResult;

/**
 * Cache configuration for prediction services.
 * Uses InMemoryCache from api-contracts with TTL support.
 */
@Configuration
public class CacheConfig {

    /**
     * Configure prediction result cache with time-based expiration.
     */
    @Bean
    public Cache<PredictionResult> predictionCache() {
        return new InMemoryCache<>(4 * 60 * 60 * 1000L); // 4 hours TTL
    }

    /**
     * Configure ETA prediction cache with shorter TTL for more frequent updates.
     */
    @Bean
    public Cache<Optional<RefundEtaPrediction>> etaPredictionCache() {
        return new InMemoryCache<>(30 * 60 * 1000L); // 30 minutes TTL
    }
}