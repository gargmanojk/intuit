package com.intuit.turbotax.api.service;

import java.util.Optional;

import com.intuit.turbotax.api.model.RefundStatus;

/**
 * Simple client interface for IRS refund status lookup.
 */
public interface ExternalIrsClient {
    
    /**
     * Gets refund status from IRS
     */
    Optional<RefundStatus> getRefundStatus(int filingId, String ssn);
}