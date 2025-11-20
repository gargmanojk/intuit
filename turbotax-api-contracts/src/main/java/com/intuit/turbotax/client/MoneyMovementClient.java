package com.intuit.turbotax.client;

import java.util.Optional;

/**
 * Simple client interface for money movement operations.
 */
public interface MoneyMovementClient {
    
    /**
     * Tracks disbursement status
     */
    Optional<String> trackDisbursement(String refundId, DisbursementMethod disbursementMethod);
    
    enum DisbursementMethod {
        DIRECT_DEPOSIT,
        PAPER_CHECK
    }
}