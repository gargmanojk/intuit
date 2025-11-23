package com.intuit.turbotax.api.v1.external.service;

import java.util.Optional;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;

/**
 * Simple client interface for state tax refund status lookup.
 */
public interface ExternalStateTaxClient {

    /**
     * Gets state refund status
     */
    Optional<RefundStatus> getRefundStatus(String filingId, Jurisdiction jurisdiction, String stateFilingId);
}