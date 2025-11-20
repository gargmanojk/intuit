package com.intuit.turbotax.refund.prediction.ml;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Implementation of RefundEtaPredictionService without caching.
 * Performs real-time ML predictions for refund processing time estimation.
 */
@Service
public class RefundEtaPredictionServiceImpl implements RefundEtaPredictionService {
    
    public RefundEtaPredictionServiceImpl() {
    }

    /**
     * Predicts refund processing time based on input features.
     * Performs real-time prediction without caching.
     */
    @Override
    public PredictionResult predict(List<RefundPredictionFeature> features) {
        // Perform prediction directly
        return performPrediction(features);
    }
    
    /**
     * Performs the actual ML prediction logic.
     */
    private PredictionResult performPrediction(List<RefundPredictionFeature> features) {
        // Simple mock: baseline 14 days, adjusted by disbursement method and amount
        double amount = 0.0;
        double daysFromFiling = 0.0;
        String disbursementMethod = null;

        if (features != null) {
            for (RefundPredictionFeature f : features) {
                if (f == null || f.getFeatureName() == null || f.getRawValue() == null) continue;
                try {
                    switch (f.getFeatureName()) {
                        case "refund_amount":                        
                            if (amount == 0.0) amount = Double.parseDouble(f.getRawValue());
                            break;
                        case "filing_date":
                            daysFromFiling = Double.parseDouble(f.getRawValue());
                            break;
                        case "refund_delivery_method":                       
                            if (disbursementMethod == null) disbursementMethod = f.getRawValue();
                            break;
                    }
                } catch (NumberFormatException e) {
                    // Ignore malformed values
                }
            }
        }

        double expectedDays = 14.0;
        
        if ("DIRECT_DEPOSIT".equalsIgnoreCase(disbursementMethod)) {
            expectedDays -= 5.0;
        }
        
        expectedDays -= Math.min(5.0, amount / 1000.0);
        expectedDays += Math.min(7.0, daysFromFiling / 30.0);
        
        expectedDays = Math.max(1.0, expectedDays);

        double confidence = 0.6 + Math.min(0.35, amount / 5000.0);
        if (daysFromFiling > 60) confidence -= 0.1;
        confidence = Math.max(0.0, Math.min(1.0, confidence));

        return new PredictionResult(expectedDays, confidence, "mock-v1");
    }
    
}
