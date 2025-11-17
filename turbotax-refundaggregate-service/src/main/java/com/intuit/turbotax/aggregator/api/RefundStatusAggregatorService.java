package com.intuit.turbotax.aggregator.api;

import java.util.Optional;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.intuit.turbotax.contract.RefundInfo;

public interface RefundStatusAggregatorService {
    @GetMapping(
        value = "/aggregate-status/{filingId}",
        produces = "application/json")
    List<RefundInfo> getRefundStatusesForFiling(String filingId);
}
