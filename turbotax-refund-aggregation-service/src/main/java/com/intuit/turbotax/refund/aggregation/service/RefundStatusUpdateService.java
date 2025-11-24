package com.intuit.turbotax.refund.aggregation.service;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.refund.aggregation.job.processors.IrsStatusProcessor;
import com.intuit.turbotax.refund.aggregation.job.processors.StateTaxStatusProcessor;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling refund status update operations.
 * Contains the business logic for updating refund statuses from external
 * services.
 */
@Service
@RequiredArgsConstructor
public class RefundStatusUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(RefundStatusUpdateService.class);

    private final RefundStatusRepository repository;
    private final IrsStatusProcessor irsProcessor;
    private final StateTaxStatusProcessor stateTaxProcessor;

    /**
     * Updates refund statuses for all active filings.
     *
     * @return the number of filings processed
     */
    public long updateAllActiveFilings() {
        LOG.info("Starting refund status update for all active filings");

        try (Stream<Integer> activeFilingIds = repository.getActiveFilingIds()) {
            long count = activeFilingIds
                    .mapToLong(filingId -> updateFilingStatus(filingId) ? 1L : 0L)
                    .sum();

            LOG.info("Completed refund status update, processed {} active filings", count);
            return count;
        }
    }

    /**
     * Updates the status for a specific filing.
     *
     * @param filingId the filing ID to update
     * @return true if the status was updated, false otherwise
     */
    public boolean updateFilingStatus(int filingId) {
        LOG.debug("Updating status for filingId={}", filingId);

        try {
            Optional<RefundStatusAggregate> currentStatus = repository.findByFilingId(filingId);
            if (currentStatus.isEmpty()) {
                LOG.debug("No existing status found for filingId={}, skipping", filingId);
                return false;
            }

            RefundStatusAggregate current = currentStatus.get();

            // Skip if already in final status
            if (current.status().isFinal()) {
                LOG.debug("FilingId={} is already in final status {}, skipping", filingId, current.status());
                return false;
            }

            // Fetch updated status from external service
            Optional<RefundStatus> updatedStatus = fetchUpdatedStatus(filingId, current.jurisdiction());

            if (updatedStatus.isPresent() && updatedStatus.get() != current.status()) {
                LOG.info("Status changed for filingId={}: {} -> {}", filingId, current.status(), updatedStatus.get());

                // Update status in repository
                RefundStatusAggregate updated = new RefundStatusAggregate(
                        current.filingId(),
                        current.trackingId(),
                        current.jurisdiction(),
                        updatedStatus.get(),
                        current.rawStatusCode(),
                        generateStatusMessage(updatedStatus.get()),
                        Instant.now(),
                        current.amount());

                repository.save(updated);
                LOG.info("Saved updated status for filingId={}", filingId);
                return true;
            }

            return false;

        } catch (Exception e) {
            LOG.error("Error updating status for filingId={}: {}", filingId, e.getMessage());
            return false;
        }
    }

    /**
     * Fetches updated status from the appropriate external service based on
     * jurisdiction.
     */
    private Optional<RefundStatus> fetchUpdatedStatus(int filingId, Jurisdiction jurisdiction) {
        return switch (jurisdiction) {
            case FEDERAL -> irsProcessor.fetchStatus(filingId);
            case STATE_CA, STATE_NY, STATE_NJ -> stateTaxProcessor.fetchStatus(filingId, jurisdiction);
            default -> {
                LOG.warn("Unknown jurisdiction {} for filingId={}", jurisdiction, filingId);
                yield Optional.empty();
            }
        };
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