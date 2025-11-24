package com.intuit.turbotax.refund.aggregation.service;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;
import com.intuit.turbotax.api.v1.refund.service.RefundDataAggregator;

/**
 * Adapter component that implements the RefundDataAggregator interface
 * by delegating to the RefundAggregationService.
 */
@Component
public class RefundAggregationServiceAdapter implements RefundDataAggregator {

    private final RefundAggregationService refundAggregationService;

    public RefundAggregationServiceAdapter(RefundAggregationService refundAggregationService) {
        this.refundAggregationService = refundAggregationService;
    }

    @Override
    public Optional<RefundStatusData> getRefundStatusForFiling(int filingId) {
        return refundAggregationService.getRefundStatusForFiling(filingId);
    }
}