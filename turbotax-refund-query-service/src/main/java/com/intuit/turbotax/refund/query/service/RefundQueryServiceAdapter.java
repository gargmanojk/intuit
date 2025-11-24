package com.intuit.turbotax.refund.query.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.intuit.turbotax.api.v1.refund.model.RefundSummary;
import com.intuit.turbotax.api.v1.refund.service.RefundStatusQueryService;

/**
 * Adapter component that implements the RefundStatusQueryService interface
 * by delegating to the RefundQueryService.
 */
@Component
public class RefundQueryServiceAdapter implements RefundStatusQueryService {

    private final RefundQueryService refundQueryService;

    public RefundQueryServiceAdapter(RefundQueryService refundQueryService) {
        this.refundQueryService = refundQueryService;
    }

    @Override
    public List<RefundSummary> getLatestRefundStatus(String userId) {
        return refundQueryService.getLatestRefundStatus(userId);
    }
}