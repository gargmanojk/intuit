package com.intuit.turbotax.api.v1.refund.service;

import java.util.Optional;

import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;

/**
 * Service interface for aggregating refund status data from multiple sources.
 * Provides a method to retrieve the aggregated refund status for a specific
 * filing.
 */
public interface RefundDataAggregator {

    /**
     * Retrieves the aggregated refund status for a given filing ID.
     *
     * @param filingId the filing identifier
     * @return an Optional containing the aggregated refund status data if found, or
     *         empty if not found
     */
    Optional<RefundStatusData> getRefundStatusForFiling(int filingId);
}