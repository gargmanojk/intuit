package com.intuit.turbotax.api.service;

import java.util.Map;
import java.util.Optional;

import com.intuit.turbotax.api.model.PredictionFeature;
import com.intuit.turbotax.api.model.RefundPrediction;

public interface RefundPredictor{
    Optional<RefundPrediction> predictEta(Map<PredictionFeature, Object> features);
}
