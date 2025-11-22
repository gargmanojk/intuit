package com.intuit.turbotax.api.model;

import java.time.LocalDate;

/**
 * Record representing a refund prediction for refund arrival
 * with confidence metrics and delivery window information.
 */
public record RefundPrediction(
    LocalDate expectedArrivalDate,
    double confidence,
    int windowDays
) {}