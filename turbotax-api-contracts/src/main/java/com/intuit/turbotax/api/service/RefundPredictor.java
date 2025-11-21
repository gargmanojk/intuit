package com.intuit.turbotax.api.service;

import java.util.Map;
import java.util.Optional;

import com.intuit.turbotax.api.model.PredictionFeature;

public interface RefundPredictor{
    Optional<Integer> predictEta(Map<PredictionFeature, Object> features);
}
