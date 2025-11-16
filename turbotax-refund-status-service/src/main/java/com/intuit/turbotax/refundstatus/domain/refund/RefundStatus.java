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

    public RefundStatus() {
    }

    public RefundStatus(String statusId, String filingId, Jurisdiction jurisdiction,
            RefundCanonicalStatus canonicalStatus, String rawStatusCode, String statusMessageKey,
            Instant statusLastUpdatedAt, BigDecimal amount) {
        this.statusId = statusId;
        this.filingId = filingId;
        this.jurisdiction = jurisdiction;
        this.canonicalStatus = canonicalStatus;
        this.rawStatusCode = rawStatusCode;
        this.statusMessageKey = statusMessageKey;
        this.statusLastUpdatedAt = statusLastUpdatedAt;
        this.amount = amount;
    }   

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getFilingId() {
        return filingId;
    }

    public void setFilingId(String filingId) {
        this.filingId = filingId;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public RefundCanonicalStatus getCanonicalStatus() {
        return canonicalStatus;
    }

    public void setCanonicalStatus(RefundCanonicalStatus canonicalStatus) {
        this.canonicalStatus = canonicalStatus;
    }

    public String getRawStatusCode() {
        return rawStatusCode;
    }

    public void setRawStatusCode(String rawStatusCode) {
        this.rawStatusCode = rawStatusCode;
    }

    public String getStatusMessageKey() {
        return statusMessageKey;
    }

    public void setStatusMessageKey(String statusMessageKey) {
        this.statusMessageKey = statusMessageKey;
    }

    public Instant getStatusLastUpdatedAt() {
        return statusLastUpdatedAt;
    }

    public void setStatusLastUpdatedAt(Instant statusLastUpdatedAt) {
        this.statusLastUpdatedAt = statusLastUpdatedAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
