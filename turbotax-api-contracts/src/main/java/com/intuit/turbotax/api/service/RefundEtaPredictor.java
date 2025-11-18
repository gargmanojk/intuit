package com.intuit.turbotax.api.service;

import java.util.List;

import com.intuit.turbotax.api.model.RefundEtaPrediction;

public interface RefundEtaPredictor {
	List<RefundEtaPrediction> predictEta(int filingId);
}