package com.intuit.turbotax.refund.aggregation.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.intuit.turbotax.refund.aggregation.service.RefundStatusUpdateService;

import lombok.RequiredArgsConstructor;

/**
 * Scheduled job that periodically fetches refund status updates from external
 * services and updates the local repository with the latest information.
 *
 * This job orchestrates the status update process but delegates the actual
 * business logic to the RefundStatusUpdateService.
 */
@Service
@RequiredArgsConstructor
public class RefundStatusUpdateJob {

    private static final Logger LOG = LoggerFactory.getLogger(RefundStatusUpdateJob.class);

    private final RefundStatusUpdateService updateService;

    /**
     * Runs periodically to fetch updated refund status from external services.
     * The interval is configurable via properties.
     */
    @Scheduled(fixedRateString = "#{${refund.aggregation.update-interval:PT30M}.toMillis()}")
    public void updateRefundStatuses() {
        LOG.info("Starting scheduled refund status update job");

        try {
            long processedCount = updateService.updateAllActiveFilings();
            LOG.info("Completed refund status update job successfully, processed {} filings", processedCount);

        } catch (Exception e) {
            LOG.error("Error during scheduled refund status update: {}", e.getMessage(), e);
        }
    }
}