package com.intuit.turbotax.api.v1.external.service;

import java.util.Map;
import java.util.Optional;

import com.intuit.turbotax.api.v1.external.model.PredictionFeature;
import com.intuit.turbotax.api.v1.external.model.RefundPrediction;

public interface RefundPredictor {
    Optional<RefundPrediction> predictEta(Map<PredictionFeature, Object> features);
}
