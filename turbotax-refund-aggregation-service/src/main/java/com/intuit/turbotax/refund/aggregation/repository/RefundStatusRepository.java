package com.intuit.turbotax.refund.aggregation.repository;

import java.util.Optional;

public interface RefundStatusRepository {
    
    Optional<RefundStatusAggregate> findByFilingId(int filingId);
}
