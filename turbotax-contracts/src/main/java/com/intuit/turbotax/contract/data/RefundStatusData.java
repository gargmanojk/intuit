package com.intuit.turbotax.contract.data;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundStatusData {
    private String filingId;
    private RefundStatus status;
    private Jurisdiction jurisdiction;
    private Instant lastUpdatedAt;
}