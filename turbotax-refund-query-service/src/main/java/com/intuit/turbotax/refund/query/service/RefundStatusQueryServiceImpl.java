package com.intuit.turbotax.refund.query.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

import com.intuit.turbotax.api.model.RefundSummary;
import com.intuit.turbotax.api.service.RefundStatusQueryService;

@RestController
public class RefundStatusQueryServiceImpl implements RefundStatusQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(RefundStatusQueryServiceImpl.class);
    private final RefundQueryOrchestrator orchestrator;

    public RefundStatusQueryServiceImpl(RefundQueryOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    @GetMapping(
        value = "/refund-status", 
        produces = "application/json")
    public Flux<RefundSummary> getLatestRefundStatus() {
        // In reality, userId comes from auth context / token
        String userId = "mock-user-id-123";

        LOG.debug("/request received from userId={}", userId);
        return Flux.fromIterable(orchestrator.getLatestRefundStatus(userId));
    }
}
