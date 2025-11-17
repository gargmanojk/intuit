package com.intuit.turbotax.aieta.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundEtaResponse {
    private LocalDate federalExpectedArrivalDate;
    private double federalConfidence;
    private int federalWindowDays;

    private LocalDate stateExpectedArrivalDate;
    private double stateConfidence;
    private int stateWindowDays;
}
