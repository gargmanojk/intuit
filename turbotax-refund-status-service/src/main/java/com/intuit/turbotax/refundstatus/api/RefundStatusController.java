package com.intuit.turbotax.refundstatus.api;

import com.intuit.turbotax.refundstatus.orchestrator.RefundStatusOrchestrator;
import com.intuit.turbotax.refundstatus.orchestrator.dto.RefundStatusResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Mocked: assume authentication middleware populates userId somewhere
@RestController
public class RefundStatusController {

    private final RefundStatusOrchestrator orchestrator;

    public RefundStatusController(RefundStatusOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping(value = "/refunds/latest-status", produces = "application/json")
    public RefundStatusResponse getLatestRefundStatus() {
        // In reality, userId comes from auth context / token
        String userId = "mock-user-id-123";
        return orchestrator.getLatestRefundStatus(userId);
        //return RefundStatusResponse.noFilingFound();
    }

    @GetMapping("/hello")
    public String hello() {
        // In reality, userId comes from auth context / token
        String userId = "mock-user-id-123";
        return "Hello mock-user-id-123\n";
    }
}
