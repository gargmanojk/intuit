package com.intuit.turbotax.refundstatus.domain.refund;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
