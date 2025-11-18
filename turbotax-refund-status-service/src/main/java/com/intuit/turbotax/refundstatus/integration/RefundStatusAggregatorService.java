package com.intuit.turbotax.refundstatus.integration;

import java.util.List;
import com.intuit.turbotax.contract.data.RefundInfo;

public interface RefundStatusAggregatorService {   
    List<RefundInfo> getRefundStatusesForFiling(String filingId);
}