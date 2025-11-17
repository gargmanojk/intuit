package com.intuit.turbotax.refundstatus.integration;

import java.util.List;
import java.util.Optional;
import com.intuit.turbotax.domainmodel.RefundInfo;

public interface RefundStatusAggregatorService {   
    List<RefundInfo> getRefundStatusesForFiling(String filingId);
}