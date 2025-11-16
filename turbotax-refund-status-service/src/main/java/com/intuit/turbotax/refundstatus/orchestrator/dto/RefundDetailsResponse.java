package com.intuit.turbotax.refundstatus.orchestrator.dto;

import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.refund.Jurisdiction;
import com.intuit.turbotax.refundstatus.domain.refund.RefundCanonicalStatus;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class RefundDetailsResponse {

    private Jurisdiction jurisdiction;
    private BigDecimal amount;
    private RefundCanonicalStatus status;
    private Instant statusLastUpdatedAt;
    private String statusMessageKey;
    private EtaPredictionResponse etaPrediction;

    public RefundDetailsResponse() {
                
    }

    public RefundDetailsResponse(Jurisdiction jurisdiction, BigDecimal amount, RefundCanonicalStatus status,
            Instant statusLastUpdatedAt, String statusMessageKey, EtaPredictionResponse etaPrediction) {
        this.jurisdiction = jurisdiction;
        this.amount = amount;
        this.status = status;
        this.statusLastUpdatedAt = statusLastUpdatedAt;
        this.statusMessageKey = statusMessageKey;
        this.etaPrediction = etaPrediction;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RefundCanonicalStatus getStatus() {
        return status;
    }

    public void setStatus(RefundCanonicalStatus status) {
        this.status = status;
    }

    public Instant getStatusLastUpdatedAt() {
        return statusLastUpdatedAt;
    }

    public void setStatusLastUpdatedAt(Instant statusLastUpdatedAt) {
        this.statusLastUpdatedAt = statusLastUpdatedAt;
    }

    public String getStatusMessageKey() {
        return statusMessageKey;
    }

    public void setStatusMessageKey(String statusMessageKey) {
        this.statusMessageKey = statusMessageKey;
    }

    public EtaPredictionResponse getEtaPrediction() {
        return etaPrediction;
    }

    public void setEtaPrediction(EtaPredictionResponse etaPrediction) {
        this.etaPrediction = etaPrediction;
    }  

    public static RefundDetailsResponse fromDomain(FilingMetadata filing, RefundStatus status, EtaPredictionResponse etaPrediction) {
        BigDecimal amount = status.getAmount() != null
                ? status.getAmount()
                : filing.getTotalRefundAmount();

        RefundDetailsResponse dto = new RefundDetailsResponse();
        dto.jurisdiction = status.getJurisdiction();
        dto.amount = amount;
        dto.status = status.getCanonicalStatus();
        dto.statusLastUpdatedAt = status.getStatusLastUpdatedAt();
        dto.statusMessageKey = status.getStatusMessageKey();
        dto.etaPrediction = etaPrediction;
        
        return dto;
    }
}
