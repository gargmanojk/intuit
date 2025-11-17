package com.intuit.turbotax.aieta.domain;

import java.util.List;

import com.intuit.turbotax.aieta.domain.EtaFeature;
import com.intuit.turbotax.aieta.domain.ModelOutput;

public interface ModelInferenceService {
    ModelOutput predict(List<EtaFeature> features);
}
