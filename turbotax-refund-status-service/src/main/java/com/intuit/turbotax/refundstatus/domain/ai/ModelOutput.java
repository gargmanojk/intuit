
package com.intuit.turbotax.refundstatus.domain.ai;

public class ModelOutput {
    private double expectedDays;
    private double confidence;
    private String modelVersion;

    public ModelOutput() {
    }

    public ModelOutput(double expectedDays, double confidence, String modelVersion) {
        this.expectedDays = expectedDays;
        this.confidence = confidence;
        this.modelVersion = modelVersion;
    }

    public double getExpectedDays() {
        return expectedDays;
    }

    public void setExpectedDays(double expectedDays) {
        this.expectedDays = expectedDays;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
}