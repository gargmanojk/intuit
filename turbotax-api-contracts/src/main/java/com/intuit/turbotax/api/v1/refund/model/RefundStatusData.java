package com.intuit.turbotax.api.v1.refund.model;

import java.time.Instant;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;

/**
 * Record representing refund status data for aggregation services.
 * Contains core status information with jurisdiction and timestamp details.
 */
public record RefundStatusData(
        int filingId,
        RefundStatus status,
        Jurisdiction jurisdiction,
        Instant lastUpdatedAt) {
}