package com.intuit.turbotax.api.service;

import java.util.Optional;

import com.intuit.turbotax.api.model.RefundEtaPrediction;

public interface RefundEtaPredictor {
	Optional<RefundEtaPrediction> predictEta(int filingId);
}