package com.intuit.turbotax.refund.prediction.ml;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building RefundPredictionFeature instances
 * with proper normalization and validation.
 */
public class RefundPredictionFeatureBuilder {
    
    private final List<RefundPredictionFeature> features = new ArrayList<>();
    
    /**
     * Adds a categorical feature (e.g., filing method, state)
     */
    public RefundPredictionFeatureBuilder addCategorical(RefundPredictionFeatureType type, String value) {
        if (value == null || value.trim().isEmpty()) {
            features.add(new RefundPredictionFeature(type, null, null, null, true, 0.0));
        } else {
            features.add(new RefundPredictionFeature(type, value.trim(), null, null, false, 1.0));
        }
        return this;
    }
    
    /**
     * Adds a numeric feature with automatic normalization
     */
    public RefundPredictionFeatureBuilder addNumeric(RefundPredictionFeatureType type, Double value) {
        if (value == null) {
            features.add(new RefundPredictionFeature(type, null, null, null, true, 0.0));
        } else {
            features.add(new RefundPredictionFeature(type, value.toString(), normalizeValue(type, value), null, false, 1.0));
        }
        return this;
    }
    
    /**
     * Adds a boolean feature (converted to 0/1)
     */
    public RefundPredictionFeatureBuilder addBoolean(RefundPredictionFeatureType type, Boolean value) {
        if (value == null) {
            features.add(new RefundPredictionFeature(type, null, null, null, true, 0.0));
        } else {
            double normalizedValue = value ? 1.0 : 0.0;
            features.add(new RefundPredictionFeature(type, value.toString(), normalizedValue, null, false, 1.0));
        }
        return this;
    }
    
    /**
     * Adds a date feature (converted to days since epoch or relative to filing season)
     */
    public RefundPredictionFeatureBuilder addDate(RefundPredictionFeatureType type, LocalDate value) {
        if (value == null) {
            features.add(new RefundPredictionFeature(type, null, null, null, true, 0.0));
        } else {
            // Convert to days since Jan 1 of current tax year
            LocalDate taxYearStart = LocalDate.of(value.getYear(), 1, 1);
            long daysSinceStart = ChronoUnit.DAYS.between(taxYearStart, value);
            features.add(new RefundPredictionFeature(type, value.toString(), (double) daysSinceStart, null, false, 1.0));
        }
        return this;
    }
    
    /**
     * Adds an amount feature with appropriate scaling
     */
    public RefundPredictionFeatureBuilder addAmount(RefundPredictionFeatureType type, Double amount) {
        if (amount == null) {
            features.add(new RefundPredictionFeature(type, null, null, null, true, 0.0));
        } else {
            // Scale amounts to thousands for better ML processing
            double scaledAmount = amount / 1000.0;
            features.add(new RefundPredictionFeature(type, amount.toString(), scaledAmount, null, false, 1.0));
        }
        return this;
    }
    
    /**
     * Adds a percentage/rate feature (0.0 to 1.0)
     */
    public RefundPredictionFeatureBuilder addPercentage(RefundPredictionFeatureType type, Double percentage) {
        if (percentage == null) {
            features.add(new RefundPredictionFeature(type, null, null, null, true, 0.0));
        } else {
            // Ensure percentage is between 0 and 1
            double normalizedPercentage = Math.max(0.0, Math.min(1.0, percentage));
            features.add(new RefundPredictionFeature(type, percentage.toString(), normalizedPercentage, null, false, 1.0));
        }
        return this;
    }
    
    /**
     * Returns the built list of features
     */
    public List<RefundPredictionFeature> build() {
        return new ArrayList<>(features);
    }
    
    /**
     * Normalizes values based on feature type and expected ranges
     */
    private double normalizeValue(RefundPredictionFeatureType type, double value) {
        return switch (type) {
            case TAXPAYER_AGE -> Math.max(0.0, Math.min(100.0, value)) / 100.0; // 0-100 years
            case DEPENDENTS_COUNT -> Math.max(0.0, Math.min(10.0, value)) / 10.0; // 0-10 dependents
            case FILING_COMPLEXITY -> Math.max(0.0, Math.min(10.0, value)) / 10.0; // 0-10 complexity score
            case FRAUD_RISK_SCORE -> Math.max(0.0, Math.min(1.0, value)); // Already 0-1
            case MATH_ERROR_PROBABILITY -> Math.max(0.0, Math.min(1.0, value)); // Already 0-1
            case TAXPAYER_HISTORY_SCORE -> Math.max(0.0, Math.min(1.0, value)); // Already 0-1
            case IRS_PROCESSING_WORKLOAD -> Math.max(0.0, Math.min(2.0, value)) / 2.0; // 0-2x normal
            case SEASONAL_FACTOR -> Math.max(0.0, Math.min(3.0, value)) / 3.0; // 0-3x normal
            case SYSTEM_CAPACITY -> Math.max(0.0, Math.min(1.0, value)); // Already 0-1
            default -> value; // No normalization for other types
        };
    }
    
    /**
     * Static factory method to start building features
     */
    public static RefundPredictionFeatureBuilder builder() {
        return new RefundPredictionFeatureBuilder();
    }
}