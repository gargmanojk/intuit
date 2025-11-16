package com.intuit.turbotax.refundstatus.orchestrator.dto;

import com.intuit.turbotax.refundstatus.domain.ai.RefundEtaPrediction;

import java.time.LocalDate;

public class EtaPredictionResponse {

    private LocalDate expectedArrivalDate;
    private double confidence;      // 0–1
    private int windowDays;
    private String explanationKey;  // for UX copy

    public EtaPredictionResponse() {}

    public static EtaPredictionResponse fromDomain(RefundEtaPrediction prediction) {
        EtaPredictionResponse dto = new EtaPredictionResponse();
        dto.expectedArrivalDate = prediction.getExpectedArrivalDate();
        dto.confidence = prediction.getConfidence();
        dto.windowDays = prediction.getWindowDays();
        dto.explanationKey = prediction.getExplanationKey();
        return dto;
    }

    // getters/setters omitted for brevity
}
