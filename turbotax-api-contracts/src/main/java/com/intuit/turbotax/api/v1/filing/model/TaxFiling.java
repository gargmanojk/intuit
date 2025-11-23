package com.intuit.turbotax.api.v1.filing.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;

/**
 * Record representing a tax filing entity with core filing information
 * and refund disbursement details.
 */
public record TaxFiling(
        int filingId,
        String trackingId, // tokenized
        Jurisdiction jurisdiction,
        String userId,
        int taxYear,
        LocalDate filingDate,
        BigDecimal refundAmount,
        PaymentMethod disbursementMethod,
        boolean isPaperless) {
}