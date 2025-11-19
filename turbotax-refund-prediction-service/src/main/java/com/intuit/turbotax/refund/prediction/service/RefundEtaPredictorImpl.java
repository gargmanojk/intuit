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
    private final Cache<Optional<RefundEtaPrediction>> etaPredictionCache;

    public RefundEtaPredictorImpl(RefundEtaPredictionService modelInferenceService, 
                                  FilingQueryService filingQueryService,
                                  Cache<Optional<RefundEtaPrediction>> etaPredictionCache, RefundPredictionFeaturesMapper mapper) {
        this.modelInferenceService = modelInferenceService;
        this.filingQueryService = filingQueryService;
        this.etaPredictionCache = etaPredictionCache;
        this.mapper = mapper;
    }

    @Override
    @GetMapping(value = "/refund-eta/{filingId}", produces = "application/json")
    public Optional<RefundEtaPrediction> predictEta(@PathVariable int filingId) {
        LOG.debug("Predicting ETA for filingId={}", filingId);
        
        // Check cache first
        String cacheKey = "eta_prediction_" + filingId;
        Optional<Optional<RefundEtaPrediction>> cachedResult = etaPredictionCache.get(cacheKey);
        if (cachedResult.isPresent()) {
            LOG.debug("Cache hit for filingId={}", filingId);
            return cachedResult.get();
        }
        
        LOG.debug("Cache miss for filingId={}, generating prediction", filingId);
        // Generate prediction if not cached
        Optional<RefundEtaPrediction> prediction = generatePrediction(filingId);
        
        // Cache the result
        etaPredictionCache.put(cacheKey, prediction);
        if (prediction.isPresent()) {
            LOG.debug("Cached prediction for filingId={}", filingId);
        } else {
            LOG.debug("No prediction generated for filingId={}", filingId);
        }
        
        return prediction;
    }
    
    /**
     * Generates ETA prediction for a filing ID.
     * Simplified to return a single prediction based on the filing's jurisdiction.
     */
    private Optional<RefundEtaPrediction> generatePrediction(int filingId) { 
        LOG.debug("Generating prediction for filingId={}", filingId);
        
        // Get filing details by filingId
        TaxFiling filing = filingQueryService.getFiling(filingId).block();
        
        if (filing == null) {
            LOG.debug("No filing found for filingId={}", filingId);
            return Optional.empty();
        }
        
        LOG.debug("Found filing for filingId={}, jurisdiction={}", filingId, filing.jurisdiction());
        
        try {
            // Generate prediction for the filing's specific jurisdiction
            List<RefundPredictionFeature> features = mapper.mapToRefundPredictionFeatures(filing, filing.jurisdiction());
            PredictionResult output = modelInferenceService.predict(features);
            RefundEtaPrediction prediction = mapper.maptToRefundEtaPrediction(output, filing, filing.jurisdiction());
            
            if (prediction != null) {
                LOG.debug("Generated prediction for jurisdiction={}, ETA={}, confidence={}", 
                         filing.jurisdiction(), prediction.expectedArrivalDate(), prediction.confidence());
                return Optional.of(prediction);
            } else {
                LOG.debug("No prediction generated for jurisdiction={}", filing.jurisdiction());
                return Optional.empty();
            }
        } catch (Exception e) {
            LOG.error("Error generating prediction for filingId={}: {}", filingId, e.getMessage());
            return Optional.empty();
        }
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

