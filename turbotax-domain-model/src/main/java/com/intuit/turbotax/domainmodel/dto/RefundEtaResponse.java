package com.intuit.turbotax.domainmodel.dto;

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
    private LocalDate expectedArrivalDate;
    private double confidence;
    private int windowDays;
}
