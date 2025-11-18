package com.intuit.turbotax.refund.aggregation.orchestration;

import java.util.List;

public interface RefundStatusRepository {
    
    List<RefundStatusAggregate> findByFilingId(String filingId);
}
