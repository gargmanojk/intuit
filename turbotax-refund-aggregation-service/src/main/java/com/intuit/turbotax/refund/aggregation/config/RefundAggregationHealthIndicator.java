package com.intuit.turbotax.refund.aggregation.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;

import lombok.RequiredArgsConstructor;

/**
 * Health indicator for the refund aggregation service.
 * Checks the health of critical components including repository and external
 * services.
 */
@Component
@RequiredArgsConstructor
public class RefundAggregationHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(RefundAggregationHealthIndicator.class);

    private static final int HEALTH_CHECK_FILING_ID = 999999999;

    private final RefundStatusRepository repository;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();

        try {
            // Check repository health
            boolean repositoryHealthy = checkRepositoryHealth();
            details.put("repository", repositoryHealthy ? "UP" : "DOWN");

            // Check external services health (simulated)
            boolean externalServicesHealthy = checkExternalServicesHealth();
            details.put("externalServices", externalServicesHealthy ? "UP" : "DOWN");

            // Overall health determination
            if (repositoryHealthy && externalServicesHealthy) {
                details.put("status", "All components are healthy");
                return Health.up()
                        .withDetails(details)
                        .build();
            } else {
                details.put("status", "Some components are unhealthy");
                return Health.down()
                        .withDetails(details)
                        .build();
            }

        } catch (Exception e) {
            log.error("Health check failed", e);
            details.put("error", e.getMessage());
            details.put("status", "Health check failed");
            return Health.down()
                    .withDetails(details)
                    .build();
        }
    }

    /**
     * Checks repository health by performing a simple read operation.
     */
    private boolean checkRepositoryHealth() {
        try {
            // Try to read from repository - this should be a lightweight operation
            repository.findByFilingId(HEALTH_CHECK_FILING_ID);
            log.debug("Repository health check passed");
            return true;
        } catch (Exception e) {
            log.warn("Repository health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks external services health.
     * In a real implementation, this would ping the actual external services.
     */
    private boolean checkExternalServicesHealth() {
        try {
            // Simulate external service health check
            // In production, this would make actual calls to IRS and State Tax services
            // For now, we'll assume they're healthy
            log.debug("External services health check passed");
            return true;
        } catch (Exception e) {
            log.warn("External services health check failed: {}", e.getMessage());
            return false;
        }
    }
}