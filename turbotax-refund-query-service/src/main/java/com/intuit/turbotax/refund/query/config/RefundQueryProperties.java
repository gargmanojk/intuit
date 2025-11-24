package com.intuit.turbotax.refund.query.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for the refund query service.
 * Allows externalized configuration of various service parameters.
 */
@Configuration
@ConfigurationProperties(prefix = "refund.query")
@Data
public class RefundQueryProperties {

    /**
     * Cache TTL for refund summaries.
     */
    private Duration cacheTtl = Duration.ofMinutes(30);

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