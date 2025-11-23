package com.intuit.turbotax.api.v1.external.service;

import java.util.Optional;

import com.intuit.turbotax.api.v1.refund.model.RefundStatus;

/**
 * Simple client interface for IRS refund status lookup.
 */
public interface ExternalIrsClient {

    /**
     * Gets refund status from IRS
     */
    Optional<RefundStatus> getRefundStatus(int filingId, String ssn);
}