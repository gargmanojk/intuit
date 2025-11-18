package com.intuit.turbotax.refund.prediction.ml;

import java.util.Objects;

/**
 * Represents a feature used in refund prediction models.
 * Contains the feature type, value, and metadata for ML processing.
 */
public class RefundPredictionFeature {
    
    /**
     * The type/category of this feature
     */
    private RefundPredictionFeatureType featureType;
    
    /**
     * The raw string value of the feature
     */
    private String rawValue;
    
    /**
     * The normalized numeric value for ML processing
     */
    private Double normalizedValue;
    
    /**
     * Weight/importance of this feature in the model
     */
    private Double weight;
    
    /**
     * Whether this feature is missing or has a default value
     */
    private boolean isMissing;
    
    /**
     * Confidence level in the feature value (0.0 to 1.0)
     */
    private Double confidence;

    public RefundPredictionFeature() {}

    public RefundPredictionFeature(RefundPredictionFeatureType featureType, String rawValue, 
                                 Double normalizedValue, Double weight, boolean isMissing, Double confidence) {
        this.featureType = featureType;
        this.rawValue = rawValue;
        this.normalizedValue = normalizedValue;
        this.weight = weight;
        this.isMissing = isMissing;
        this.confidence = confidence;
    }

    public static RefundPredictionFeatureBuilder builder() {
        return new RefundPredictionFeatureBuilder();
    }

    // Getters
    public RefundPredictionFeatureType getFeatureType() { return featureType; }
    public String getRawValue() { return rawValue; }
    public Double getNormalizedValue() { return normalizedValue; }
    public Double getWeight() { return weight; }
    public boolean isMissing() { return isMissing; }
    public Double getConfidence() { return confidence; }

    // Setters
    public void setFeatureType(RefundPredictionFeatureType featureType) { this.featureType = featureType; }
    public void setRawValue(String rawValue) { this.rawValue = rawValue; }
    public void setNormalizedValue(Double normalizedValue) { this.normalizedValue = normalizedValue; }
    public void setWeight(Double weight) { this.weight = weight; }
    public void setMissing(boolean missing) { isMissing = missing; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    
    /**
     * Gets the feature name for ML model input
     */
    public String getFeatureName() {
        return featureType != null ? featureType.getFeatureName() : null;
    }
    
    /**
     * Gets the description of this feature
     */
    public String getDescription() {
        return featureType != null ? featureType.getDescription() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefundPredictionFeature that = (RefundPredictionFeature) o;
        return isMissing == that.isMissing &&
               featureType == that.featureType &&
               Objects.equals(rawValue, that.rawValue) &&
               Objects.equals(normalizedValue, that.normalizedValue) &&
               Objects.equals(weight, that.weight) &&
               Objects.equals(confidence, that.confidence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureType, rawValue, normalizedValue, weight, isMissing, confidence);
    }

    @Override
    public String toString() {
        return "RefundPredictionFeature{" +
               "featureType=" + featureType +
               ", rawValue='" + rawValue + '\'' +
               ", normalizedValue=" + normalizedValue +
               ", weight=" + weight +
               ", isMissing=" + isMissing +
               ", confidence=" + confidence +
               '}';
    }

    public static class RefundPredictionFeatureBuilder {
        private RefundPredictionFeatureType featureType;
        private String rawValue;
        private Double normalizedValue;
        private Double weight;
        private boolean isMissing;
        private Double confidence;

        public RefundPredictionFeatureBuilder featureType(RefundPredictionFeatureType featureType) {
            this.featureType = featureType;
            return this;
        }

        public RefundPredictionFeatureBuilder rawValue(String rawValue) {
            this.rawValue = rawValue;
            return this;
        }

        public RefundPredictionFeatureBuilder normalizedValue(Double normalizedValue) {
            this.normalizedValue = normalizedValue;
            return this;
        }

        public RefundPredictionFeatureBuilder weight(Double weight) {
            this.weight = weight;
            return this;
        }

        public RefundPredictionFeatureBuilder isMissing(boolean isMissing) {
            this.isMissing = isMissing;
            return this;
        }

        public RefundPredictionFeatureBuilder confidence(Double confidence) {
            this.confidence = confidence;
            return this;
        }

        public RefundPredictionFeature build() {
            return new RefundPredictionFeature(featureType, rawValue, normalizedValue, 
                                             weight, isMissing, confidence);
        }
    }
}
