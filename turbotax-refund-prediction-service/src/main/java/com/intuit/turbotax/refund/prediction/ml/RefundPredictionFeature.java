package com.intuit.turbotax.refund.prediction.ml;

/**
 * Represents a feature used in refund prediction models.
 * Contains the feature type, value, and metadata for ML processing.
 *
 * @param featureType The type/category of this feature
 * @param rawValue The raw string value of the feature
 * @param normalizedValue The normalized numeric value for ML processing
 * @param weight Weight/importance of this feature in the model
 * @param isMissing Whether this feature is missing or has a default value
 * @param confidence Confidence level in the feature value (0.0 to 1.0)
 */
public record RefundPredictionFeature(
    RefundPredictionFeatureType featureType,
    String rawValue,
    Double normalizedValue,
    Double weight,
    boolean isMissing,
    Double confidence
) {
    /**
     * Gets the feature name for ML model input
     */
    public String getFeatureName() {
        return featureType != null ? featureType.getFeatureName() : null;
    }

}
