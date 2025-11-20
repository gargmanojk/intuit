package com.intuit.turbotax.refund.aggregation.client;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intuit.turbotax.api.model.RefundStatus;
import com.intuit.turbotax.api.model.Jurisdiction;
import com.intuit.turbotax.client.ExternalStateTaxClient;

@Component  
class ExternalStateTaxClientImpl implements ExternalStateTaxClient {
    
    private static final Logger log = LoggerFactory.getLogger(ExternalStateTaxClientImpl.class);
    
    @Override
    public Optional<RefundStatus> getRefundStatus(String filingId, Jurisdiction jurisdiction, String stateFilingId) {
        log.info("Getting state refund status for filing: {}, jurisdiction: {}", filingId, jurisdiction);
        
        // Simple mock: return ACCEPTED for CA, FILED for others
        RefundStatus status = (jurisdiction == Jurisdiction.STATE_CA) ? RefundStatus.ACCEPTED : RefundStatus.FILED;
        
        return Optional.of(status);
    }
}