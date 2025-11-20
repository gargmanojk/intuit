package com.intuit.turbotax.client;

import java.util.Optional;

import com.intuit.turbotax.api.model.RefundStatus;
import com.intuit.turbotax.api.model.Jurisdiction;

/**
 * Simple client interface for state tax refund status lookup.
 */
public interface ExternalStateTaxClient {
    
    /**
     * Gets state refund status
     */
    Optional<RefundStatus> getRefundStatus(String filingId, Jurisdiction jurisdiction, String stateFilingId);
}