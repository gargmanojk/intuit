package com.intuit.turbotax.refundstatus.domain.ai;

import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;

import org.springframework.stereotype.Component;

public interface FeatureStoreClient {
    
    EtaFeatures loadFeatures(FilingMetadata filing, RefundStatus status);
}

@Component
class FeatureStoreClientImpl implements FeatureStoreClient { 

    public EtaFeatures loadFeatures(FilingMetadata filing, RefundStatus status) {
        return new EtaFeatures();
    }
}

