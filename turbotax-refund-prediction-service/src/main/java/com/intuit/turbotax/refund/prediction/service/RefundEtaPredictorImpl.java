package com.intuit.turbotax.refund.prediction.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.refund.prediction.ml.RefundPredictionFeature;
import com.intuit.turbotax.refund.prediction.ml.RefundPredictionFeatureType;
import com.intuit.turbotax.refund.prediction.ml.PredictionResult;
import com.intuit.turbotax.api.model.RefundPredictionInput;
import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.service.RefundEtaPredictor;
import com.intuit.turbotax.refund.prediction.ml.RefundEtaPredictionService;

@RestController
public class RefundEtaPredictorImpl implements RefundEtaPredictor {
    private final RefundEtaPredictionService modelInferenceService;

    public RefundEtaPredictorImpl(RefundEtaPredictionService modelInferenceService) {
        this.modelInferenceService = modelInferenceService;
    }

    @Override
    @GetMapping(value = "/refund-eta", produces = "application/json")
    public Optional<RefundEtaPrediction> predictEta(@ModelAttribute RefundPredictionInput predictionInput) { 
        List<RefundPredictionFeature> features = mapToRefundPredictionFeatures(predictionInput);
        PredictionResult output = modelInferenceService.predict(features);
        RefundEtaPrediction resp = buildResponse(output, predictionInput);
        return Optional.ofNullable(resp);
    }

    /**
     * Map RefundEtaRequest properties to a list of RefundPredictionFeature key-value pairs.
     * Transforms request data into feature names and values for ML model consumption.
     * 
     * @param req the RefundEtaRequest containing filing and refund data
     * @return List of RefundPredictionFeature objects representing engineered features
     */
    private List<RefundPredictionFeature> mapToRefundPredictionFeatures(RefundPredictionInput req) {
        List<RefundPredictionFeature> features = new ArrayList<>();
        
        if (req == null) {
            return features;
        }

        // Tax year - using categorical feature since it's discrete
        if (req.getTaxYear() > 0) {
            features.add(RefundPredictionFeature.of(RefundPredictionFeatureType.FILING_DATE, String.valueOf(req.getTaxYear())));
        }

        // Jurisdiction - maps to state filed
        if (req.getJurisdiction() != null) {
            features.add(RefundPredictionFeature.of(RefundPredictionFeatureType.STATE_FILED, req.getJurisdiction().name()));
        }

        // Refund amount
        if (req.getRefundAmount() != null) {
            String amountStr = req.getRefundAmount().toString();
            features.add(RefundPredictionFeature.of(RefundPredictionFeatureType.REFUND_AMOUNT, amountStr, req.getRefundAmount().doubleValue()));
        }

        // Return status - maps to filing complexity or method
        if (req.getReturnStatus() != null) {
            String statusName = req.getReturnStatus().name();
            features.add(RefundPredictionFeature.of(RefundPredictionFeatureType.FILING_METHOD, statusName));
        }   

        // Disbursement method - maps to refund delivery method
        if (req.getDisbursementMethod() != null) {
            String methodName = req.getDisbursementMethod().name();
            features.add(RefundPredictionFeature.of(RefundPredictionFeatureType.REFUND_DELIVERY_METHOD, methodName));
        }

        // Days from filing
        if (req.getFilingDate() != null) {
            long daysFromFiling = ChronoUnit.DAYS.between(req.getFilingDate(), LocalDate.now());
            features.add(RefundPredictionFeature.of(RefundPredictionFeatureType.FILING_DATE, String.valueOf(daysFromFiling), (double) daysFromFiling));
        }

        return features;
    }        

    /**
     * Build a RefundEtaPrediction using model output and request context.
     * Maps the prediction to the appropriate jurisdiction fields.
     */
    private RefundEtaPrediction buildResponse(PredictionResult output, RefundPredictionInput req) {
        if (output == null) {
            return null;
        }

        RefundEtaPrediction.RefundEtaPredictionBuilder b = RefundEtaPrediction.builder();
        
        LocalDate expectedDate = LocalDate.now().plusDays((long) output.getExpectedDays());
        double confidence = output.getConfidence();
        int windowDays = (int) Math.ceil(output.getExpectedDays() * 0.15); // 15% window

         b.expectedArrivalDate(expectedDate)
            .confidence(confidence)
            .windowDays(windowDays);

        return b.build();
    }
}

