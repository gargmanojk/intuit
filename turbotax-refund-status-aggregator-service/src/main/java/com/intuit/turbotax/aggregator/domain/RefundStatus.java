package com.intuit.turbotax.aggregator.domain;

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
public class RefundStatus {
    
    private String statusId;
    private String filingId;
    private Jurisdiction jurisdiction;
    private RefundCanonicalStatus canonicalStatus;
    private String rawStatusCode;
    private String statusMessageKey;
    private Instant statusLastUpdatedAt;
    private BigDecimal amount;    
}