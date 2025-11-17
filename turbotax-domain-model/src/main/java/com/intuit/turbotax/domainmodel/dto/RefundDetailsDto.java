package com.intuit.turbotax.domainmodel.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.domainmodel.Jurisdiction;
import com.intuit.turbotax.domainmodel.RefundCanonicalStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundDetailsDto {
    private Jurisdiction jurisdiction;
    private BigDecimal amount;
    private RefundCanonicalStatus status;
    private Instant statusLastUpdatedAt;
    private String statusMessageKey;
    private EtaPredictionDto etaPrediction;
}