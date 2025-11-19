package com.intuit.turbotax.refund.aggregation.orchestration;

import java.util.List;
import java.util.Optional;

public interface RefundStatusRepository {
    
    Optional<RefundStatusAggregate> findByFilingId(int filingId);
}
