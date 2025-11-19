package com.intuit.turbotax.api.service;

import reactor.core.publisher.Flux;

import com.intuit.turbotax.api.model.RefundSummary;

public interface RefundStatusQueryService {
    Flux<RefundSummary> getLatestRefundStatus();
}