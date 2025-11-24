package com.intuit.turbotax.refund.query.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.api.v1.refund.model.RefundSummary;
import com.intuit.turbotax.refund.query.service.RefundQueryServiceAdapter;

/**
 * REST controller for refund query operations.
 * Provides endpoints for retrieving refund status information.
 */
@RestController
@RequestMapping("/api/v1")
public class RefundQueryController {

    private static final Logger LOG = LoggerFactory.getLogger(RefundQueryController.class);

    private final RefundQueryServiceAdapter refundQueryServiceAdapter;

    public RefundQueryController(RefundQueryServiceAdapter refundQueryServiceAdapter) {
        this.refundQueryServiceAdapter = refundQueryServiceAdapter;
    }

    @GetMapping(value = "/refund-status", produces = "application/json")
    public List<RefundSummary> getRefundStatus(@RequestHeader("X-USER-ID") String userId) {
        LOG.debug("Getting refund status for userId={}", userId);
        return refundQueryServiceAdapter.getLatestRefundStatus(userId);
    }
}