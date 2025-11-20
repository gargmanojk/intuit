package com.intuit.turbotax.refund.aggregation.repository;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Repository interface for managing refund status aggregates.
 * Provides methods to query, update, and retrieve refund status information for tax filings.
 */
public interface RefundStatusRepository {
    
    /**
     * Finds the refund status aggregate for a specific filing ID.
     *
     * @param filingId the unique filing identifier
     * @return an Optional containing the refund status aggregate if found, empty otherwise
     */
    Optional<RefundStatusAggregate> findByFilingId(int filingId);
    
    /**
     * Returns a stream of active filing IDs that need status updates.
     * Only returns filings that are not in a final status.
     *
     * @return a stream of filing IDs for filings that are not in a final status
     */
    Stream<Integer> getActiveFilingIds();
    
    /**
     * Saves or updates a refund status aggregate in the repository.
     *
     * @param aggregate the refund status aggregate to save or update
     */
    void save(RefundStatusAggregate aggregate);
}
