package com.intuit.turbotax.refund.prediction.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundPredictionFeature {
    private String name;
    private String value;
}
