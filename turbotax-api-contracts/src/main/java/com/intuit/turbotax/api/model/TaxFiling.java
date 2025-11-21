package com.intuit.turbotax.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Record representing a tax filing entity with core filing information
 * and refund disbursement details.
 */
public record TaxFiling(
    int filingId,
    String trackingId,  // tokenized
    Jurisdiction jurisdiction,
    String userId,
    int taxYear,
    LocalDate filingDate,
    BigDecimal refundAmount,
    PaymentMethod disbursementMethod,
    boolean isPaperless
) {}