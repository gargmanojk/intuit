package com.intuit.turbotax.refund.prediction.ml;

import java.util.List;

public interface ModelInferenceService {
    ModelOutput predict(List<EtaFeature> features);
}
