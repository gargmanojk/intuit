package com.intuit.turbotax.refund.prediction.ml;

import java.util.List;

/**
 * Service interface for predicting the estimated time of arrival (ETA) for tax refunds.
 * Consumes a list of prediction features and returns a prediction result.
 */
public interface RefundEtaPredictionService {
    /**
     * Predicts the estimated time of arrival (ETA) for a tax refund based on the provided features.
     *
     * @param features a list of features describing the tax filing and user context
     * @return a prediction result containing the estimated refund ETA and related information
     * @throws IllegalArgumentException if features is null or empty
     */
    PredictionResult predict(List<RefundPredictionFeature> features);
}
