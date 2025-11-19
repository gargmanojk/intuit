package com.intuit.turbotax.refund.prediction.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.refund.prediction.ml.RefundPredictionFeature;
import com.intuit.turbotax.refund.prediction.ml.RefundPredictionFeatureType;
import com.intuit.turbotax.refund.prediction.ml.PredictionResult;
import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.model.Jurisdiction;
import com.intuit.turbotax.api.service.RefundEtaPredictor;
import com.intuit.turbotax.api.service.FilingQueryService;
import com.intuit.turbotax.api.service.Cache;
import com.intuit.turbotax.refund.prediction.ml.RefundEtaPredictionService;

/**
 * REST controller implementation of RefundEtaPredictor with caching support.
 * Caches ETA predictions at the service level to reduce computation overhead.
 */
@RestController
public class RefundEtaPredictorImpl implements RefundEtaPredictor {
    private static final Logger LOG = LoggerFactory.getLogger(RefundEtaPredictorImpl.class);
    
    RefundPredictionFeaturesMapper mapper;
    private final RefundEtaPredictionService modelInferenceService;
    private final FilingQueryService filingQueryService;
    private final Cache<List<RefundEtaPrediction>> etaPredictionCache;

    public RefundEtaPredictorImpl(RefundEtaPredictionService modelInferenceService, 
                                  FilingQueryService filingQueryService,
                                  Cache<List<RefundEtaPrediction>> etaPredictionCache, RefundPredictionFeaturesMapper mapper) {
        this.modelInferenceService = modelInferenceService;
        this.filingQueryService = filingQueryService;
        this.etaPredictionCache = etaPredictionCache;
        this.mapper = mapper;
    }

    @Override
    @GetMapping(value = "/refund-eta/{filingId}", produces = "application/json")
    public List<RefundEtaPrediction> predictEta(@PathVariable int filingId) {
        LOG.debug("Predicting ETA for filingId={}", filingId);
        
        // Check cache first
        String cacheKey = "eta_prediction_" + filingId;
        Optional<List<RefundEtaPrediction>> cachedResult = etaPredictionCache.get(cacheKey);
        if (cachedResult.isPresent()) {
            LOG.debug("Cache hit for filingId={}, returning {} predictions", filingId, cachedResult.get().size());
            return cachedResult.get();
        }
        
        LOG.debug("Cache miss for filingId={}, generating predictions", filingId);
        // Generate predictions if not cached
        List<RefundEtaPrediction> predictions = generatePredictions(filingId);
        
        // Cache the results if any predictions were generated
        if (!predictions.isEmpty()) {
            etaPredictionCache.put(cacheKey, predictions);
            LOG.debug("Cached {} predictions for filingId={}", predictions.size(), filingId);
        } else {
            LOG.debug("No predictions generated for filingId={}", filingId);
        }
        
        return predictions;
    }
    
    /**
     * Generates ETA predictions for a filing ID.
     * Separated from main method to support caching logic.
     */
    private List<RefundEtaPrediction> generatePredictions(int filingId) { 
        LOG.debug("Generating predictions for filingId={}", filingId);
        
        // Get filing details by filingId
        List<TaxFiling> filings = filingQueryService.findLatestFilingForUser(String.valueOf(filingId));
        
        if (filings.isEmpty()) {
            LOG.debug("No filings found for filingId={}", filingId);
            return List.of();
        }
        
        TaxFiling filing = filings.get(0);
        LOG.debug("Found filing for filingId={}, jurisdiction={}", filingId, filing.jurisdiction());
        
        List<RefundEtaPrediction> predictions = new ArrayList<>();
        
        // Generate predictions for relevant jurisdictions
        List<Jurisdiction> jurisdictions = getRelevantJurisdictions(filing);
        LOG.debug("Generating predictions for {} jurisdictions: {}", jurisdictions.size(), jurisdictions);
        
        for (Jurisdiction jurisdiction : jurisdictions) {
            LOG.debug("Processing jurisdiction={} for filingId={}", jurisdiction, filingId);
            List<RefundPredictionFeature> features = mapper.mapToRefundPredictionFeatures(filing, jurisdiction);
            PredictionResult output = modelInferenceService.predict(features);
            RefundEtaPrediction prediction = mapper.maptToRefundEtaPrediction(output, filing, jurisdiction);
            
            if (prediction != null) {
                LOG.debug("Generated prediction for jurisdiction={}, ETA={}, confidence={}", 
                         jurisdiction, prediction.expectedArrivalDate(), prediction.confidence());
                predictions.add(prediction);
            } else {
                LOG.debug("No prediction generated for jurisdiction={}", jurisdiction);
            }
        }
        
        LOG.debug("Generated {} total predictions for filingId={}", predictions.size(), filingId);
        return predictions;
    }

    /**
     * Determine relevant jurisdictions for ETA prediction based on filing information.
     * Returns federal plus any applicable state jurisdictions.
     * 
     * @param filing the TaxFiling containing jurisdiction and filing data
     * @return List of Jurisdiction objects for which to generate predictions
     */
    private List<Jurisdiction> getRelevantJurisdictions(TaxFiling filing) {
        List<Jurisdiction> jurisdictions = new ArrayList<>();
        
        // Always include federal jurisdiction
        jurisdictions.add(Jurisdiction.FEDERAL);
        
        // Add state jurisdiction if available and different from federal
        if (filing.jurisdiction() != null && filing.jurisdiction() != Jurisdiction.FEDERAL) {
            jurisdictions.add(filing.jurisdiction());
        }
        
        return jurisdictions;
    }
}

