package com.intuit.turbotax.refundstatus.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.refundstatus.dto.RefundStatusResponse;

@RestController
public class RefundStatusServiceImpl implements RefundStatusService {
    private static final Logger LOG = LoggerFactory.getLogger(RefundStatusServiceImpl.class);
    private final RefundStatusOrchestrator orchestrator;

    public RefundStatusServiceImpl(RefundStatusOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public RefundStatusResponse getLatestRefundStatus() {
        // In reality, userId comes from auth context / token
        String userId = "mock-user-id-123";

        LOG.debug("/request received from userId={}", userId);
        return orchestrator.getLatestRefundStatus(userId);
    }
}
