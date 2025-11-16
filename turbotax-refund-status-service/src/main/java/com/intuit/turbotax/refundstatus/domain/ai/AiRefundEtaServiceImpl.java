package com.intuit.turbotax.refundstatus.domain.ai;

import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AiRefundEtaServiceImpl implements AiRefundEtaService {

    private final FeatureStoreClient featureStoreClient;
    private final ModelInferenceClient modelInferenceClient;

    public AiRefundEtaServiceImpl(FeatureStoreClient featureStoreClient,
                                  ModelInferenceClient modelInferenceClient) {
        this.featureStoreClient = featureStoreClient;
        this.modelInferenceClient = modelInferenceClient;
    }

    @Override
    public RefundEtaPrediction predictEta(FilingMetadata filing, RefundStatus status) {
        EtaFeatures features = featureStoreClient.loadFeatures(filing, status);

        ModelOutput output = modelInferenceClient.predict(features);

        RefundEtaPrediction prediction = new RefundEtaPrediction();
        prediction.setExpectedArrivalDate(LocalDate.now().plusDays((long) output.getExpectedDays()));
        prediction.setConfidence(output.getConfidence());
        prediction.setWindowDays(3);
        prediction.setExplanationKey("IRS_EFILE_DIRECT_DEPOSIT_TYPICAL");
        prediction.setModelVersion(output.getModelVersion());
        
        return prediction;
    }
}
