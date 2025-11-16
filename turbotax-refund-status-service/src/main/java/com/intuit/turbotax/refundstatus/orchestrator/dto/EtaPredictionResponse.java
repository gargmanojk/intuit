package com.intuit.turbotax.refundstatus.orchestrator.dto;

import java.time.LocalDate;

import com.intuit.turbotax.refundstatus.domain.ai.RefundEtaPrediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtaPredictionResponse {
    private LocalDate expectedArrivalDate;
    private double confidence;      // 0–1
    private int windowDays;
    private String explanationKey;  // for UX copy

    public static EtaPredictionResponse fromDomain(RefundEtaPrediction prediction) {
        EtaPredictionResponse dto = new EtaPredictionResponse();
        dto.expectedArrivalDate = prediction.getExpectedArrivalDate();
        dto.confidence = prediction.getConfidence();
        dto.windowDays = prediction.getWindowDays();
        dto.explanationKey = prediction.getExplanationKey();
        
        return dto;
    }
}
