package com.intuit.turbotax.refund.aggregation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;
import com.intuit.turbotax.refund.aggregation.service.RefundAggregationServiceAdapter;

@RestController
@RequestMapping("/api/v1")
public class RefundAggregationController {

    private static final Logger LOG = LoggerFactory.getLogger(RefundAggregationController.class);

    private final RefundAggregationServiceAdapter refundAggregationServiceAdapter;

    public RefundAggregationController(RefundAggregationServiceAdapter refundAggregationServiceAdapter) {
        this.refundAggregationServiceAdapter = refundAggregationServiceAdapter;
    }

    @GetMapping(value = "/aggregate-status/filings/{filingId}", produces = "application/json")
    public RefundStatusData getRefundStatusForFiling(@PathVariable int filingId) {
        LOG.debug("Getting refund status for filingId={}", filingId);
        return refundAggregationServiceAdapter.getRefundStatusForFiling(filingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
