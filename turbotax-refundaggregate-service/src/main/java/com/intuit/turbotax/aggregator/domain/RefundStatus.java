package com.intuit.turbotax.aggregator.domain;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.contract.Jurisdiction;
import com.intuit.turbotax.contract.RefundCanonicalStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundStatus { 
    private String filingId;
    private String trackingId;  // tokenized
    private Jurisdiction jurisdiction;
    private RefundCanonicalStatus status;
    private String rawStatusCode;
    private String messageKey;
    private Instant lastUpdatedAt;
    private BigDecimal amount;
}