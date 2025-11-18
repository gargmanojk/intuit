package com.intuit.turbotax.aggregator.domain;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.contract.data.Jurisdiction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundStatus { 
    private String filingId;
    private String trackingId;  // tokenized
    private Jurisdiction jurisdiction;
    private com.intuit.turbotax.contract.data.RefundStatus status;
    private String rawStatusCode;
    private String messageKey;
    private Instant lastUpdatedAt;
    private BigDecimal amount;
}