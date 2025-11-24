package com.intuit.turbotax.refund.aggregation.config;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.context.annotation.Configuration;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * Configuration class for initializing RefundStatusRepository with sample data.
 * This separates the initialization logic from the repository implementation.
 */
@Configuration
@RequiredArgsConstructor
public class RefundStatusRepositoryConfig {

    private final RefundStatusRepository repository;

    /**
     * Initializes the repository with sample data after construction.
     */
    @PostConstruct
    public void initializeRepository() {
        initializeSampleData();
    }

    /**
     * Initializes the repository with sample refund status data.
     */
    private void initializeSampleData() {
        // Add some sample federal filings
        repository.save(new RefundStatusAggregate(
                202410001,
                "IRS-TRACK-202410001",
                Jurisdiction.FEDERAL,
                RefundStatus.PROCESSING,
                "FED_202410001",
                "Your federal tax refund is being processed by the IRS",
                Instant.now(),
                BigDecimal.valueOf(2500)));

        repository.save(new RefundStatusAggregate(
                202410002,
                "CA-TRACK-202410002",
                Jurisdiction.STATE_CA,
                RefundStatus.ACCEPTED,
                "CA_202410002",
                "Your California state tax refund has been accepted",
                Instant.now(),
                BigDecimal.valueOf(350)));

        repository.save(new RefundStatusAggregate(
                202410003,
                "IRS-TRACK-202410003",
                Jurisdiction.FEDERAL,
                RefundStatus.FILED,
                "FED_202410003",
                "Your federal tax return has been filed",
                Instant.now(),
                BigDecimal.valueOf(1800)));

        repository.save(new RefundStatusAggregate(
                202410004,
                "NY-TRACK-202410004",
                Jurisdiction.STATE_NY,
                RefundStatus.PROCESSING,
                "NY_202410004",
                "Your New York state tax refund is being processed",
                Instant.now(),
                BigDecimal.valueOf(450)));
    }
}