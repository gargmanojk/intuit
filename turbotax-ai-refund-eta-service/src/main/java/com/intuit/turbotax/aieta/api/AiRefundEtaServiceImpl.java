package com.intuit.turbotax.aieta.api;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.aieta.domain.EtaFeature;
import com.intuit.turbotax.aieta.domain.ModelOutput;
import com.intuit.turbotax.contract.AiFeatures;
import com.intuit.turbotax.contract.EtaRefundInfo;
import com.intuit.turbotax.aieta.domain.ModelInferenceService;

@RestController
public class AiRefundEtaServiceImpl implements AiRefundEtaService {
    private final ModelInferenceService modelInferenceService;

    public AiRefundEtaServiceImpl(ModelInferenceService modelInferenceService) {
        this.modelInferenceService = modelInferenceService;
    }

    @Override
    public Optional<EtaRefundInfo> predictEta(AiFeatures aiFeatures) { 
        List<EtaFeature> features = mapToEtaFeatures(aiFeatures);
        ModelOutput output = modelInferenceService.predict(features);
        EtaRefundInfo resp = buildResponse(output, aiFeatures);
        return Optional.ofNullable(resp);
    }

    /**
     * Map RefundEtaRequest properties to a list of EtaFeature key-value pairs.
     * Transforms request data into feature names and values for ML model consumption.
     * 
     * @param req the RefundEtaRequest containing filing and refund data
     * @return List of EtaFeature objects representing engineered features
     */
    private List<EtaFeature> mapToEtaFeatures(AiFeatures req) {
        List<EtaFeature> features = new ArrayList<>();
        
        if (req == null) {
            return features;
        }

        // Tax year
        if (req.getTaxYear() > 0) {
            features.add(new EtaFeature("taxYear", String.valueOf(req.getTaxYear())));
        }

        // Jurisdiction
        if (req.getJurisdiction() != null) {
            features.add(new EtaFeature("jurisdiction", req.getJurisdiction().name()));
        }

        // Refund amount
        if (req.getRefundAmount() != null) {
            String amountStr = req.getRefundAmount().toString();
            features.add(new EtaFeature("refundAmount", amountStr));
        }

        // Return status
        if (req.getReturnStatus() != null) {
            String statusName = req.getReturnStatus().name();
            features.add(new EtaFeature("returnStatus", statusName));
        }   


        // Disbursement method
        if (req.getDisbursementMethod() != null) {
            String methodName = req.getDisbursementMethod().name();
            features.add(new EtaFeature("disbursementMethod", methodName));
        }

        // Days from filing
        if (req.getFilingDate() != null) {
            long daysFromFiling = ChronoUnit.DAYS.between(req.getFilingDate(), LocalDate.now());
            features.add(new EtaFeature("daysFromFiling", String.valueOf(daysFromFiling)));
        }

        return features;
    }        

    /**
     * Build a EtaRefundInfo using model output and request context.
     * Maps the prediction to the appropriate jurisdiction fields.
     */
    private EtaRefundInfo buildResponse(ModelOutput output, AiFeatures req) {
        if (output == null) {
            return null;
        }

        EtaRefundInfo.EtaRefundInfoBuilder b = EtaRefundInfo.builder();
        
        LocalDate expectedDate = LocalDate.now().plusDays((long) output.getExpectedDays());
        double confidence = output.getConfidence();
        int windowDays = (int) Math.ceil(output.getExpectedDays() * 0.15); // 15% window

         b.expectedArrivalDate(expectedDate)
            .confidence(confidence)
            .windowDays(windowDays);

        return b.build();
    }
}

