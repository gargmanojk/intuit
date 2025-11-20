package com.intuit.turbotax.refund.aggregation.repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface RefundStatusRepository {
    
    Optional<RefundStatusAggregate> findByFilingId(int filingId);
    
    /**
     * Returns a stream of active filing IDs that need status updates.
     * Only returns filings that are not in final status.
     */
    Stream<Integer> getActiveFilingIds();
    
    /**
     * Saves or updates a refund status aggregate.
     */
    void save(RefundStatusAggregate aggregate);
}
