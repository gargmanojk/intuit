package com.intuit.turbotax.api.service;

import java.util.Optional;

import com.intuit.turbotax.api.model.RefundEtaPrediction;

/**
 * Service interface for predicting the estimated time of arrival (ETA) for a tax refund.
 * Provides a method to predict the refund ETA for a specific filing.
 */
public interface RefundEtaPredictor {

	/**
	 * Predicts the estimated time of arrival (ETA) for a refund based on the filing ID.
	 *
	 * @param filingId the filing identifier
	 * @return an Optional containing the refund ETA prediction if available, or empty if not found
	 */
	Optional<RefundEtaPrediction> predictEta(int filingId);
}