package com.intuit.turbotax.filing.query.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.intuit.turbotax.filing.query.repository.TaxFilingRepository;

/**
 * Health indicator for the filing data repository.
 * Checks if the filing data is available and accessible.
 */
@Component
public class FilingDataHealthIndicator implements HealthIndicator {

    private final TaxFilingRepository repository;

    public FilingDataHealthIndicator(TaxFilingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        try {
            // Check if we can access the repository
            long filingCount = repository.findLatestByUserId("health-check-user").count();

            return Health.up()
                    .withDetail("filingDataStatus", "available")
                    .withDetail("totalFilings", filingCount)
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("filingDataStatus", "unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}