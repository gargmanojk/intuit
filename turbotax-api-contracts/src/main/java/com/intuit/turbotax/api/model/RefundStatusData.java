package com.intuit.turbotax.api.model;

import java.time.Instant;

/**
 * Record representing refund status data for aggregation services.
 * Contains core status information with jurisdiction and timestamp details.
 */
public record RefundStatusData(
    int filingId,
    RefundStatus status,
    Jurisdiction jurisdiction,
    Instant lastUpdatedAt
) {}