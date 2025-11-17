package com.intuit.turbotax.contract;

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
public class RefundInfo {
    private String filingId;
    private RefundCanonicalStatus status;
    private Jurisdiction jurisdiction;
    private Instant lastUpdatedAt;
}