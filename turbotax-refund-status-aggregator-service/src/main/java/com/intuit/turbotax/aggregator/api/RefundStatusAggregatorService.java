package com.intuit.turbotax.aggregator.api;

import java.util.List;

import com.intuit.turbotax.aggregator.dto.RefundStatusAggregatorResponse;

public interface RefundStatusAggregatorService {
    RefundStatusAggregatorResponse getRefundStatusesForFiling(String filingId);
}
