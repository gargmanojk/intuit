package com.intuit.turbotax.refund.aggregation.job;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.intuit.turbotax.api.v1.external.service.ExternalIrsClient;
import com.intuit.turbotax.api.v1.external.service.ExternalStateTaxClient;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;

/**
 * Scheduled job that periodically fetches refund status updates from external
 * services
 * and updates the local repository with the latest information.
 */
@Service
public class RefundStatusUpdateJob {

    private static final Logger LOG = LoggerFactory.getLogger(RefundStatusUpdateJob.class);

    private final RefundStatusRepository repository;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateTaxClient;

    public RefundStatusUpdateJob(RefundStatusRepository repository,
            ExternalIrsClient irsClient,
            ExternalStateTaxClient stateTaxClient) {
        this.repository = repository;
        this.irsClient = irsClient;
        this.stateTaxClient = stateTaxClient;
    }

    /**
     * Runs every 30 minutes to fetch updated refund status from external services.
     * Updates local repository with the latest information.
     */
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutes in milliseconds
    public void updateRefundStatuses() {
        LOG.info("Starting scheduled refund status update job");

        try {
            // Get all active filing IDs that need status updates
            try (Stream<Integer> activeFilingIds = repository.getActiveFilingIds()) {
                long count = activeFilingIds.peek(filingId -> updateFilingStatus(filingId)).count();
                LOG.info("Processed {} active filings for status updates", count);
            }

            LOG.info("Completed refund status update job successfully");

        } catch (Exception e) {
            LOG.error("Error during scheduled refund status update: {}", e.getMessage(), e);
        }
    }

    /**
     * Updates the status for a specific filing by contacting external services.
     */
    private void updateFilingStatus(int filingId) {
        LOG.debug("Updating status for filingId={}", filingId);

        try {
            // Get current status from repository
            Optional<RefundStatusAggregate> currentStatus = repository.findByFilingId(filingId);
            if (currentStatus.isEmpty()) {
                LOG.debug("No existing status found for filingId={}, skipping", filingId);
                return;
            }

            RefundStatusAggregate current = currentStatus.get();

            // Skip if already in final status
            if (current.status().isFinal()) {
                LOG.debug("FilingId={} is already in final status {}, skipping", filingId, current.status());
                return;
            }

            // Fetch updated status from external service
            RefundStatus updatedStatus = fetchUpdatedStatus(filingId, current);

            if (updatedStatus != null && updatedStatus != current.status()) {
                LOG.info("Status changed for filingId={}: {} -> {}", filingId, current.status(), updatedStatus);

                // Update status in repository
                RefundStatusAggregate updated = new RefundStatusAggregate(
                        current.filingId(),
                        current.trackingId(),
                        current.jurisdiction(),
                        updatedStatus,
                        current.rawStatusCode(),
                        generateStatusMessage(updatedStatus),
                        Instant.now(),
                        current.amount());

                repository.save(updated);
                LOG.info("Saved updated status for filingId={}", filingId);
            }

        } catch (Exception e) {
            LOG.error("Error updating status for filingId={}: {}", filingId, e.getMessage());
        }
    }

    /**
     * Fetches updated status from external service based on jurisdiction.
     */
    private RefundStatus fetchUpdatedStatus(int filingId, RefundStatusAggregate current) {
        try {
            switch (current.jurisdiction()) {
                case FEDERAL:
                    return irsClient.getRefundStatus(filingId, "***-**-1234").orElse(null);
                case STATE_CA:
                case STATE_NY:
                case STATE_NJ:
                    return stateTaxClient
                            .getRefundStatus(String.valueOf(filingId), current.jurisdiction(), String.valueOf(filingId))
                            .orElse(null);
                default:
                    LOG.warn("Unknown jurisdiction {} for filingId={}", current.jurisdiction(), filingId);
                    return null;
            }
        } catch (Exception e) {
            LOG.error("Error fetching status for filingId={}: {}", filingId, e.getMessage());
            return null;
        }
    }

    /**
     * Generates a user-friendly status message based on the refund status.
     */
    private String generateStatusMessage(RefundStatus status) {
        return switch (status) {
            case FILED -> "Your tax return has been filed";
            case ACCEPTED -> "Your tax return has been accepted";
            case PROCESSING -> "Your refund is being processed";
            case SENT_TO_BANK -> "Your refund has been sent";
            case DEPOSITED -> "Your refund has been deposited";
            case ERROR -> "Error processing your refund";
            default -> "Status: " + status;
        };
    }
}