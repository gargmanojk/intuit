package com.intuit.turbotax.refund.prediction.ml;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal, concise builder for RefundPredictionFeature.
 */
public class RefundPredictionFeatureBuilder {
    private final List<RefundPredictionFeature> features = new ArrayList<>();

    public RefundPredictionFeatureBuilder add(RefundPredictionFeatureType type, Object value) {
        if (value == null || (value instanceof String s && s.trim().isEmpty())) {
            features.add(missing(type));
            return this;
        }
        if (value instanceof Boolean b) {
            features.add(new RefundPredictionFeature(type, b.toString(), b ? 1.0 : 0.0, null, false, 1.0));
        } else if (value instanceof Number n) {
            features.add(new RefundPredictionFeature(type, n.toString(), normalize(type, n.doubleValue()), null, false, 1.0));
        } else if (value instanceof LocalDate d) {
            LocalDate taxYearStart = LocalDate.of(d.getYear(), 1, 1);
            long days = ChronoUnit.DAYS.between(taxYearStart, d);
            features.add(new RefundPredictionFeature(type, d.toString(), (double) days, null, false, 1.0));
        } else {
            features.add(new RefundPredictionFeature(type, value.toString(), null, null, false, 1.0));
        }
        return this;
    }

    public List<RefundPredictionFeature> build() {
        return new ArrayList<>(features);
    }

    private RefundPredictionFeature missing(RefundPredictionFeatureType type) {
        return new RefundPredictionFeature(type, null, null, null, true, 0.0);
    }

    private double normalize(RefundPredictionFeatureType type, double value) {
        return switch (type) {
            case TAXPAYER_AGE -> Math.max(0.0, Math.min(100.0, value)) / 100.0;
            case DEPENDENTS_COUNT, FILING_COMPLEXITY -> Math.max(0.0, Math.min(10.0, value)) / 10.0;
            case FRAUD_RISK_SCORE, MATH_ERROR_PROBABILITY, TAXPAYER_HISTORY_SCORE, SYSTEM_CAPACITY -> Math.max(0.0, Math.min(1.0, value));
            case IRS_PROCESSING_WORKLOAD -> Math.max(0.0, Math.min(2.0, value)) / 2.0;
            case SEASONAL_FACTOR -> Math.max(0.0, Math.min(3.0, value)) / 3.0;
            case REFUND_AMOUNT, ADJUSTED_GROSS_INCOME, TOTAL_TAX, WITHHOLDING_AMOUNT, PRIOR_YEAR_REFUND -> value / 1000.0;
            default -> value;
        };
    }

    public static RefundPredictionFeatureBuilder builder() {
        return new RefundPredictionFeatureBuilder();
    }
}