package com.intuit.turbotax.refundstatus.domain.refund;

import java.util.List;

public interface RefundStatusAggregatorService {

    List<RefundStatus> getRefundStatusesForFiling(String filingId);
}
