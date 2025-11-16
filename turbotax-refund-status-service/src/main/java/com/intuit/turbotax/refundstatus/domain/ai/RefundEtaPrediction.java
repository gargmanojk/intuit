package com.intuit.turbotax.refundstatus.domain.ai;

import java.time.LocalDate;

public class RefundEtaPrediction {

    private LocalDate expectedArrivalDate;
    private double confidence;
    private int windowDays;
    private String explanationKey;
    private String modelVersion;

    // getters/setters...

    public LocalDate getExpectedArrivalDate() {
        return expectedArrivalDate;
    }

    public void setExpectedArrivalDate(LocalDate expectedArrivalDate) {
        this.expectedArrivalDate = expectedArrivalDate;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public int getWindowDays() {
        return windowDays;
    }

    public void setWindowDays(int windowDays) {
        this.windowDays = windowDays;
    }

    public String getExplanationKey() {
        return explanationKey;
    }

    public void setExplanationKey(String explanationKey) {
        this.explanationKey = explanationKey;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
}
