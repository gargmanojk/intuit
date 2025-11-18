package com.intuit.turbotax.aieta.domain;

import java.util.List;

public interface ModelInferenceService {
    ModelOutput predict(List<EtaFeature> features);
}
