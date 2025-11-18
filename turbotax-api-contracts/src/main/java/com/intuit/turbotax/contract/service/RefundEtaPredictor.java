package com.intuit.turbotax.contract.service;

import java.util.Optional;

import com.intuit.turbotax.contract.data.RefundPredictionInput;
import com.intuit.turbotax.contract.data.RefundEtaPrediction;

public interface RefundEtaPredictor {
	Optional<RefundEtaPrediction> predictEta(RefundPredictionInput predictionInput);
}