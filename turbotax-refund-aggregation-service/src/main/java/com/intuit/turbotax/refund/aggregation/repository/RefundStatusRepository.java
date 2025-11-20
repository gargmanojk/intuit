package com.intuit.turbotax.refund.aggregation.repository;

import java.util.List;
import java.util.Optional;

public interface RefundStatusRepository {
    
    Optional<RefundStatusAggregate> findByFilingId(int filingId);
    
    /**
     * Returns a list of active filing IDs that need status updates.
     * Only returns filings that are not in final status.
     */
    List<Integer> getActiveFilingIds();
}
