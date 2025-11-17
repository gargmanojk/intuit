package com.intuit.turbotax.aieta.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelOutput {
    private double expectedDays;
    private double confidence;
    private String modelVersion;
}
