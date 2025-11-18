package com.intuit.turbotax.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Record representing input data for refund ETA prediction models.
 * Contains all necessary features for ML-based prediction algorithms.
 */
public record RefundPredictionInput(
    int taxYear,    
    Jurisdiction jurisdiction, 
    LocalDate filingDate,
    BigDecimal refundAmount,
    RefundStatus returnStatus,    
    PaymentMethod disbursementMethod // DIRECT_DEPOSIT, CARD, CHECK...
    // Add more features as needed
) {}
