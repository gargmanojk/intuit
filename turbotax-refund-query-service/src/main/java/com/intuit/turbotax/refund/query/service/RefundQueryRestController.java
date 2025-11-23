package com.intuit.turbotax.refund.query.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.api.v1.refund.model.RefundSummary;
import com.intuit.turbotax.api.v1.refund.service.RefundStatusQueryService;

@RestController
public class RefundQueryRestController implements RefundStatusQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(RefundQueryRestController.class);
    private final RefundQueryOrchestrator orchestrator;

    public RefundQueryRestController(RefundQueryOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    @GetMapping(value = "/refund-status", produces = "application/json")
    public List<RefundSummary> getLatestRefundStatus(@RequestHeader("X-USER-ID") String userId) {

        LOG.debug("/refund-status request received from userId={}", userId);
        List<RefundSummary> refundSummaries = orchestrator.getLatestRefundStatus(userId);
        LOG.debug("Retrieved {} refund summaries for userId={}", refundSummaries.size(), userId);

        return refundSummaries;
    }
}
