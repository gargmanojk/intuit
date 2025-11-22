package com.intuit.turbotax.api.service;

import java.util.Map;
import java.util.Optional;

import com.intuit.turbotax.api.model.PredictionFeature;
import com.intuit.turbotax.api.model.RefundEtaPrediction;

public interface RefundPredictor{
    Optional<RefundEtaPrediction> predictEta(Map<PredictionFeature, Object> features);
}
