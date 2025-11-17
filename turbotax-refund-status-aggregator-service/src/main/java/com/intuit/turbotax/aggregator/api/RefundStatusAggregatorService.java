package com.intuit.turbotax.aggregator.api;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.intuit.turbotax.domainmodel.dto.RefundStatusAggregatorDto;

public interface RefundStatusAggregatorService {
    @GetMapping(
        value = "/aggregate-status/{filingId}",
        produces = "application/json")
    Optional<RefundStatusAggregatorDto> getRefundStatusesForFiling(String filingId);
}
