package com.intuit.turbotax.refundstatus.orchestrator.dto;

import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.refund.Jurisdiction;
import com.intuit.turbotax.refundstatus.domain.refund.RefundCanonicalStatus;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundDetailsResponse {

    private Jurisdiction jurisdiction;
    private BigDecimal amount;
    private RefundCanonicalStatus status;
    private Instant statusLastUpdatedAt;
    private String statusMessageKey;
    private EtaPredictionResponse etaPrediction;
    
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
