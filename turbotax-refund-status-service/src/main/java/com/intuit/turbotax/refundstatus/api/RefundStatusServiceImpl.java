package com.intuit.turbotax.refundstatus.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.contract.data.RefundSummaryInfo;
import com.intuit.turbotax.contract.service.RefundStatusQueryService;

@RestController
public class RefundStatusQueryServiceImpl implements RefundStatusQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(RefundStatusQueryServiceImpl.class);
    private final RefundStatusOrchestrator orchestrator;

    public RefundStatusQueryServiceImpl(RefundStatusOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    @GetMapping(
        value = "/refund-status", 
        produces = "application/json")
    public List<RefundSummaryInfo> getLatestRefundStatus() {
        // In reality, userId comes from auth context / token
        String userId = "mock-user-id-123";

        LOG.debug("/request received from userId={}", userId);
        return orchestrator.getLatestRefundStatus(userId);
    }
}
