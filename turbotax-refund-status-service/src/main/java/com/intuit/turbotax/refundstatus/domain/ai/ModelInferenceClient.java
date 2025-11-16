package com.intuit.turbotax.refundstatus.domain.ai;

import org.springframework.stereotype.Component;

public interface ModelInferenceClient {
    ModelOutput predict(EtaFeatures features);
}

@Component
class ModelInferenceClientImpl implements ModelInferenceClient { 
    
    public ModelOutput predict(EtaFeatures features) {
        return new ModelOutput(3, 0.85, "GPT-5");
    }
}