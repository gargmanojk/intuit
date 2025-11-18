package com.intuit.turbotax.refund.aggregation.orchestration;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.api.model.Jurisdiction;
import com.intuit.turbotax.api.model.RefundStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundStatusAggregate { 
    private int filingId;
    private String trackingId;  // tokenized
    private Jurisdiction jurisdiction;
    private RefundStatus status;
    private String rawStatusCode;
    private String messageKey;
    private Instant lastUpdatedAt;
    private BigDecimal amount;
}