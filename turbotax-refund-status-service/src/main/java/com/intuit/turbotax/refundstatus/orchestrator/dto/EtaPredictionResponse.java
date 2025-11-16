package com.intuit.turbotax.refundstatus.orchestrator.dto;

import java.time.LocalDate;

import com.intuit.turbotax.refundstatus.domain.ai.RefundEtaPrediction;

public class EtaPredictionResponse {
    private LocalDate expectedArrivalDate;
    private double confidence;      // 0–1
    private int windowDays;
    private String explanationKey;  // for UX copy

    public EtaPredictionResponse() {
    }

    public EtaPredictionResponse(LocalDate expectedArrivalDate, double confidence, int windowDays,
            String explanationKey) {
        this.expectedArrivalDate = expectedArrivalDate;
        this.confidence = confidence;
        this.windowDays = windowDays;
        this.explanationKey = explanationKey;
    }

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

    public static EtaPredictionResponse fromDomain(RefundEtaPrediction prediction) {
        EtaPredictionResponse dto = new EtaPredictionResponse();
        dto.expectedArrivalDate = prediction.getExpectedArrivalDate();
        dto.confidence = prediction.getConfidence();
        dto.windowDays = prediction.getWindowDays();
        dto.explanationKey = prediction.getExplanationKey();
        
        return dto;
    }
}
