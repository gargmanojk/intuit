package com.intuit.turbotax.refund.prediction.ml;

/**
 * Immutable result from a refund prediction model.
 * Contains prediction metrics and model information.
 */
public record PredictionResult(
    double expectedDays,
    double confidence,
    String modelVersion
) {}
