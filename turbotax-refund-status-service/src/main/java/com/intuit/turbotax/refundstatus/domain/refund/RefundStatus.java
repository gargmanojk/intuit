package com.intuit.turbotax.refundstatus.domain.refund;

import java.math.BigDecimal;
import java.time.Instant;

public class RefundStatus {

    private String statusId;
    private String filingId;
    private Jurisdiction jurisdiction;
    private RefundCanonicalStatus canonicalStatus;
    private String rawStatusCode;
    private String statusMessageKey;
    private Instant statusLastUpdatedAt;
    private BigDecimal amount;

    public RefundStatus() {}

    // getters/setters...

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public RefundCanonicalStatus getCanonicalStatus() {
        return canonicalStatus;
    }

    public Instant getStatusLastUpdatedAt() {
        return statusLastUpdatedAt;
    }

    public String getStatusMessageKey() {
        return statusMessageKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
