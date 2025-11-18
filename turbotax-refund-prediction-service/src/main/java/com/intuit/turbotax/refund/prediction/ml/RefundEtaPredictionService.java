package com.intuit.turbotax.refund.prediction.ml;

import java.util.List;

public interface RefundEtaPredictionService {
    PredictionResult predict(List<RefundPredictionFeature> features);
}
