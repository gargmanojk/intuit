package com.intuit.turbotax.refund.prediction.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
import com.intuit.turbotax.refund.prediction.ml.RefundEtaPredictionService;

@RestController
public class RefundEtaPredictorImpl implements RefundEtaPredictor {
    private final RefundEtaPredictionService modelInferenceService;
    private final FilingQueryService filingQueryService;

    public RefundEtaPredictorImpl(RefundEtaPredictionService modelInferenceService, 
                                  FilingQueryService filingQueryService) {
        this.modelInferenceService = modelInferenceService;
        this.filingQueryService = filingQueryService;
    }

    @Override
    @GetMapping(value = "/refund-eta/{filingId}", produces = "application/json")
    public List<RefundEtaPrediction> predictEta(@PathVariable int filingId) { 
        // Get filing details by filingId
        List<TaxFiling> filings = filingQueryService.findLatestFilingForUser(String.valueOf(filingId));
        
        if (filings.isEmpty()) {
            return List.of();
        }
        
        TaxFiling filing = filings.get(0);
        List<RefundEtaPrediction> predictions = new ArrayList<>();
        
        // Generate predictions for relevant jurisdictions
        List<Jurisdiction> jurisdictions = getRelevantJurisdictions(filing);
        
        for (Jurisdiction jurisdiction : jurisdictions) {
            List<RefundPredictionFeature> features = mapToRefundPredictionFeatures(filing, jurisdiction);
            PredictionResult output = modelInferenceService.predict(features);
            RefundEtaPrediction prediction = buildResponse(output, filing, jurisdiction);
            
            if (prediction != null) {
                predictions.add(prediction);
            }
        }
        
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

    /**
     * Map TaxFiling properties to a list of RefundPredictionFeature key-value pairs.
     * Transforms filing data into feature names and values for ML model consumption.
     * 
     * @param filing the TaxFiling containing filing and refund data
     * @param jurisdiction the specific jurisdiction for this prediction
     * @return List of RefundPredictionFeature objects representing engineered features
     */
    private List<RefundPredictionFeature> mapToRefundPredictionFeatures(TaxFiling filing, Jurisdiction jurisdiction) {
        List<RefundPredictionFeature> features = new ArrayList<>();
        
        if (filing == null) {
            return features;
        }

        // Tax year - using categorical feature since it's discrete
        if (filing.taxYear() > 0) {
            features.add(new RefundPredictionFeature(RefundPredictionFeatureType.FILING_DATE, String.valueOf(filing.taxYear()), null, null, false, 1.0));
        }

        // Jurisdiction - maps to state filed
        if (jurisdiction != null) {
            features.add(new RefundPredictionFeature(RefundPredictionFeatureType.STATE_FILED, jurisdiction.name(), null, null, false, 1.0));
        }

        // Refund amount
        if (filing.refundAmount() != null) {
            String amountStr = filing.refundAmount().toString();
            features.add(new RefundPredictionFeature(RefundPredictionFeatureType.REFUND_AMOUNT, amountStr, filing.refundAmount().doubleValue(), null, false, 1.0));
        }

        // Disbursement method - maps to refund delivery method
        if (filing.disbursementMethod() != null) {
            String methodName = filing.disbursementMethod().name();
            features.add(new RefundPredictionFeature(RefundPredictionFeatureType.REFUND_DELIVERY_METHOD, methodName, null, null, false, 1.0));
        }

        // Days from filing
        if (filing.filingDate() != null) {
            long daysFromFiling = ChronoUnit.DAYS.between(filing.filingDate(), LocalDate.now());
            features.add(new RefundPredictionFeature(RefundPredictionFeatureType.FILING_DATE, String.valueOf(daysFromFiling), (double) daysFromFiling, null, false, 1.0));
        }

        return features;
    }        

    /**
     * Build a RefundEtaPrediction using model output and filing context.
     * Maps the prediction to the appropriate jurisdiction fields.
     */
    private RefundEtaPrediction buildResponse(PredictionResult output, TaxFiling filing, Jurisdiction jurisdiction) {
        if (output == null) {
            return null;
        }

        LocalDate expectedDate = LocalDate.now().plusDays((long) output.expectedDays());
        double confidence = output.confidence();
        int windowDays = (int) Math.ceil(output.expectedDays() * 0.15); // 15% window

        return new RefundEtaPrediction(
            expectedDate,
            confidence,
            windowDays
        );
    }
}

