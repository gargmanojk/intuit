package com.intuit.turbotax.refundstatus.domain.refund;

import java.util.List;

public interface RefundStatusRepository {
    
    List<RefundStatus> findByFilingId(String filingId);
}