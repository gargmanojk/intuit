package com.intuit.turbotax.api.v1.refund.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;

/**
 * Record representing a complete refund summary with filing details,
 * status information, and ETA prediction data.
 */
public record RefundSummary(
        int filingId,
        String trackingId, // tokenized
        Integer taxYear,
        LocalDate filingDate,
        Jurisdiction jurisdiction,
        BigDecimal amount,
        RefundStatus status,
        PaymentMethod disbursementMethod, // DIRECT_DEPOSIT, CARD, CHECK...
        Instant lastUpdatedAt,
        // ETA prediction details
        LocalDate etaDate,
        double etaConfidence,
        int etaWindowDays) {
}