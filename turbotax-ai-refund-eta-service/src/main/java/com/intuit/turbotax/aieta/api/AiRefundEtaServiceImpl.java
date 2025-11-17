package com.intuit.turbotax.aieta.api;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.RestController;

import com.intuit.turbotax.aieta.api.AiRefundEtaService;
import com.intuit.turbotax.aieta.domain.EtaFeature;
import com.intuit.turbotax.aieta.domain.ModelOutput;
import com.intuit.turbotax.aieta.dto.RefundEtaRequest;
import com.intuit.turbotax.aieta.dto.RefundEtaResponse;
import com.intuit.turbotax.aieta.domain.ModelInferenceService;

@RestController
public class AiRefundEtaServiceImpl implements AiRefundEtaService {
    private final ModelInferenceService modelInferenceService;

    public AiRefundEtaServiceImpl(ModelInferenceService modelInferenceService) {
        this.modelInferenceService = modelInferenceService;
    }

    @Override
    public Optional<RefundEtaResponse> predictEta(RefundEtaRequest req) { 
        List<EtaFeature> federalFeatures = mapToEtaFeatures(req, true, false);
        List<EtaFeature> stateFeatures = mapToEtaFeatures(req, false, true);

        ModelOutput federalOutput = modelInferenceService.predict(federalFeatures);
        ModelOutput stateOutput = modelInferenceService.predict(stateFeatures);
        RefundEtaResponse resp = buildResponse(federalOutput, stateOutput);
        return Optional.ofNullable(resp);
    }

    /**
     * Map RefundEtaRequest properties to a list of EtaFeature key-value pairs.
     * Transforms request data into feature names and values for ML model consumption.
     * 
     * @param req the RefundEtaRequest containing filing and refund data
     * @return List of EtaFeature objects representing engineered features
     */
    private List<EtaFeature> mapToEtaFeatures(RefundEtaRequest req, boolean isFederal, boolean isState) {
        List<EtaFeature> features = new ArrayList<>();
        
        // Tax year feature
        features.add(EtaFeature.builder()
                .name("taxYear")
                .value(String.valueOf(req.getTaxYear()))
                .build());
        
        // Days from filing
        if (req.getFilingDate() != null) {
            long daysFromFiling = ChronoUnit.DAYS.between(req.getFilingDate(), LocalDate.now());
            features.add(EtaFeature.builder()
                    .name("daysFromFiling")
                    .value(String.valueOf(daysFromFiling))
                    .build());
        }
        
        if (isFederal) {
            // Federal refund features
            if (req.getFederalRefundAmount() != null) {
                features.add(EtaFeature.builder()
                        .name("federalRefundAmount")
                        .value(req.getFederalRefundAmount().toPlainString())
                        .build());
            }
            
            if (req.getFederalReturnStatus() != null) {
                features.add(EtaFeature.builder()
                        .name("federalReturnStatus")
                        .value(req.getFederalReturnStatus().toString())
                        .build());
            }
            
            if (req.getFederalDisbursementMethod() != null) {
                features.add(EtaFeature.builder()
                        .name("federalDisbursementMethod")
                        .value(req.getFederalDisbursementMethod())
                        .build());
            }
        } 
        
        if (isState) { 
        
            // State refund features
            if (req.getStateRefundAmount() != null) {
                features.add(EtaFeature.builder()
                        .name("stateRefundAmount")
                        .value(req.getStateRefundAmount().toPlainString())
                        .build());
            }
            
            if (req.getStateJurisdiction() != null) {
                features.add(EtaFeature.builder()
                        .name("stateJurisdiction")
                        .value(req.getStateJurisdiction().toString())
                        .build());
            }
            
            if (req.getStateReturnStatus() != null) {
                features.add(EtaFeature.builder()
                        .name("stateReturnStatus")
                        .value(req.getStateReturnStatus().toString())
                        .build());
            }
            
            if (req.getStateDisbursementMethod() != null) {
                features.add(EtaFeature.builder()
                        .name("stateDisbursementMethod")
                        .value(req.getStateDisbursementMethod())
                        .build());
            }
        }

        return features;
    }        

    /**
     * Build a RefundEtaResponse using model outputs for federal and state.
     * If an output is null, the corresponding fields will be left null/zero.
     */
    private RefundEtaResponse buildResponse(ModelOutput federal, ModelOutput state) {
        RefundEtaResponse.RefundEtaResponseBuilder b = RefundEtaResponse.builder();

        if (federal != null) {
            b.federalExpectedArrivalDate(LocalDate.now().plusDays((long) federal.getExpectedDays()))
             .federalConfidence(federal.getConfidence())
             .federalWindowDays(3);
        } else {
            b.federalExpectedArrivalDate(null).federalConfidence(0.0).federalWindowDays(0);
        }

        if (state != null) {
            b.stateExpectedArrivalDate(LocalDate.now().plusDays((long) state.getExpectedDays()))
             .stateConfidence(state.getConfidence())
             .stateWindowDays(3);
        } else {
            b.stateExpectedArrivalDate(null).stateConfidence(0.0).stateWindowDays(0);
        }

        return b.build();
    }
}

