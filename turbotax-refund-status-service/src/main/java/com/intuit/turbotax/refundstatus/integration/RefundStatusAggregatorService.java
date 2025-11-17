package com.intuit.turbotax.refundstatus.integration;

import java.util.Optional;
import com.intuit.turbotax.domainmodel.dto.RefundStatusAggregatorDto;

public interface RefundStatusAggregatorService {   
    Optional<RefundStatusAggregatorDto> getRefundStatusesForFiling(String filingId);
}