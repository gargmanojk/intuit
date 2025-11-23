package com.intuit.turbotax.refund.aggregation.repository;

import java.math.BigDecimal;
import java.time.Instant;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;

/**
 * Immutable aggregate representing refund status information.
 * Contains filing details, jurisdiction, status, and metadata.
 */
public record RefundStatusAggregate(
        int filingId,
        String trackingId, // tokenized
        Jurisdiction jurisdiction,
        RefundStatus status,
        String rawStatusCode,
        String messageKey,
        Instant lastUpdatedAt,
        BigDecimal amount) {
}