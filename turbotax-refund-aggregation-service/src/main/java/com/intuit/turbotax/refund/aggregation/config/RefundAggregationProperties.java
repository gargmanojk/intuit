package com.intuit.turbotax.refund.aggregation.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for the refund aggregation service.
 * Allows externalized configuration of various service parameters.
 */
@Configuration
@ConfigurationProperties(prefix = "refund.aggregation")
@Data
public class RefundAggregationProperties {

    /**
     * Interval for scheduled status updates.
     */
    private Duration updateInterval = Duration.ofMinutes(30);

    /**
     * Maximum number of retry attempts for external service calls.
     */
    private int maxRetryAttempts = 3;

    /**
     * Delay between retry attempts.
     */
    private Duration retryDelay = Duration.ofSeconds(5);

    /**
     * Whether to enable circuit breaker for external service calls.
     */
    private boolean enableCircuitBreaker = true;

    /**
     * Timeout for external service calls.
     */
    private Duration externalServiceTimeout = Duration.ofSeconds(30);
}