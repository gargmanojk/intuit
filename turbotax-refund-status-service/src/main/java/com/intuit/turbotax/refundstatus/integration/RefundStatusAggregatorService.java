package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;
import com.intuit.turbotax.refundstatus.dto.RefundStatusAggregatorResponse;

public interface RefundStatusAggregatorService {   
    Optional<RefundStatusAggregatorResponse> getRefundStatusesForFiling(String filingId);
}