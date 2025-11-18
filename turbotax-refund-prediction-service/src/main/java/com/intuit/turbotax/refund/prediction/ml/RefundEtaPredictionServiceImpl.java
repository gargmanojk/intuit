package com.intuit.turbotax.refund.prediction.ml;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.intuit.turbotax.api.service.Cache;

/**
 * Implementation of RefundEtaPredictionService with custom caching support.
 * Uses Cache interface from api-contracts for prediction result caching.
 */
@Service
public class RefundEtaPredictionServiceImpl implements RefundEtaPredictionService {
    
    private final Cache<PredictionResult> predictionCache;
    
    public RefundEtaPredictionServiceImpl(Cache<PredictionResult> predictionCache) {
        this.predictionCache = predictionCache;
    }

    /**
     * Predicts refund processing time based on input features.
     * Checks cache first, performs prediction if cache miss, then caches result.
     */
    @Override
    public PredictionResult predict(List<RefundPredictionFeature> features) {
        // Generate cache key from features
        String cacheKey = generateFeatureHash(features);
        
        // Check cache first
        Optional<PredictionResult> cachedResult = predictionCache.get(cacheKey);
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }
        
        // Perform prediction if not cached
        PredictionResult result = performPrediction(features);
        
        // Cache result if confidence is high enough
        if (result.confidence() >= 0.5) {
            predictionCache.put(cacheKey, result);
        }
        
        return result;
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
    
    /**
     * Clears all cached prediction results if supported by cache implementation.
     */
    public void clearPredictionCache() {
        // Note: Cache interface doesn't have clear method, 
        // this would need to be implemented in concrete cache class
    }
    
    /**
     * Generates a stable cache key from prediction features.
     * Ensures consistent hashing for identical feature sets.
     */
    public String generateFeatureHash(List<RefundPredictionFeature> features) {
        if (features == null || features.isEmpty()) {
            return "empty_features";
        }
        
        // Sort features by type name for consistent ordering
        return features.stream()
                .filter(f -> f != null && f.getFeatureType() != null)
                .sorted((a, b) -> a.getFeatureType().name().compareTo(b.getFeatureType().name()))
                .map(f -> f.getFeatureType().name() + ":" + f.getRawValue() + ":" + f.getNormalizedValue())
                .collect(Collectors.joining("|"));
    }
}
