package com.intuit.turbotax.refund.prediction.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a feature used in refund prediction models.
 * Contains the feature type, value, and metadata for ML processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    
    /**
     * Creates a feature with just type and raw value
     */
    public static RefundPredictionFeature of(RefundPredictionFeatureType type, String value) {
        return RefundPredictionFeature.builder()
                .featureType(type)
                .rawValue(value)
                .isMissing(false)
                .confidence(1.0)
                .build();
    }
    
    /**
     * Creates a feature with type, raw value, and normalized value
     */
    public static RefundPredictionFeature of(RefundPredictionFeatureType type, String rawValue, Double normalizedValue) {
        return RefundPredictionFeature.builder()
                .featureType(type)
                .rawValue(rawValue)
                .normalizedValue(normalizedValue)
                .isMissing(false)
                .confidence(1.0)
                .build();
    }
    
    /**
     * Creates a missing feature placeholder
     */
    public static RefundPredictionFeature missing(RefundPredictionFeatureType type) {
        return RefundPredictionFeature.builder()
                .featureType(type)
                .isMissing(true)
                .confidence(0.0)
                .build();
    }
    
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
}
