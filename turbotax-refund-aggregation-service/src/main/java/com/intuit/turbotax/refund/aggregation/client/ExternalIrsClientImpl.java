package com.intuit.turbotax.refund.aggregation.client;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intuit.turbotax.api.model.RefundStatus;
import com.intuit.turbotax.client.ExternalIrsClient;

@Primary
@Component
public class ExternalIrsClientImpl implements ExternalIrsClient {
    
    private static final Logger log = LoggerFactory.getLogger(ExternalIrsClientImpl.class);
    
    @Override
    public Optional<RefundStatus> getRefundStatus(int filingId, String ssn) {
        log.info("Getting IRS refund status for filing: {}", filingId);
        
        // Simple mock: return PROCESSING for even IDs, SENT_TO_BANK for odd
        RefundStatus status = (filingId % 2 == 0) ? RefundStatus.PROCESSING : RefundStatus.SENT_TO_BANK;
        
        return Optional.of(status);
    }
}