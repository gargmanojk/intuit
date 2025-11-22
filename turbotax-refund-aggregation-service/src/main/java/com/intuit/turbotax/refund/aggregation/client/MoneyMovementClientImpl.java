package com.intuit.turbotax.refund.aggregation.client;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.intuit.turbotax.api.service.MoneyMovementClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
class MoneyMovementClientImpl implements MoneyMovementClient {
    
    private static final Logger log = LoggerFactory.getLogger(MoneyMovementClientImpl.class);
    
    @Override
    public Optional<String> trackDisbursement(String refundId, DisbursementMethod disbursementMethod) {
        log.info("Tracking disbursement for refund: {}, method: {}", refundId, disbursementMethod);
        
        // Simple mock: return tracking status
        String status = (disbursementMethod == DisbursementMethod.DIRECT_DEPOSIT) ? "DEPOSITED" : "MAILED";
        
        return Optional.of(status);
    }
}