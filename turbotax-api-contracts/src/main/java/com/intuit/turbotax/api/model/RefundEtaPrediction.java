package com.intuit.turbotax.api.model;

import java.time.LocalDate;

/**
 * Record representing an ETA prediction for refund arrival
 * with confidence metrics and delivery window information.
 */
public record RefundEtaPrediction(
    LocalDate expectedArrivalDate,
    double confidence,
    int windowDays
) {}