package com.intuit.turbotax.aggregator.domain;

import java.util.List;

public interface RefundStatusRepository {
    
    List<RefundStatus> findByFilingId(String filingId);
}
