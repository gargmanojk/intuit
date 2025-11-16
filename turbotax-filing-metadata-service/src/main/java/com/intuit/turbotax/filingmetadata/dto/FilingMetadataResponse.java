package com.intuit.turbotax.filingmetadata.dto;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.filingmetadata.domain.FilingMetadata;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilingMetadataResponse {
    private String filingId;
    private String userId;
    private int taxYear;
    private BigDecimal totalRefundAmount;
    private BigDecimal federalRefundAmount;
    private BigDecimal stateRefundAmountTotal;    
    private String irsTrackingId;  // tokenized
    private String disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...

    public static Optional<FilingMetadataResponse> fromEntity(Optional<FilingMetadata> entity) {  
        if (entity == null || !entity.isPresent()) {
            return Optional.empty();
        }

        FilingMetadata fmEntity = entity.get();

        FilingMetadataResponse fm = FilingMetadataResponse.builder()
                .filingId(fmEntity.getFilingId())
                .userId(fmEntity.getUserId())
                .taxYear(fmEntity.getTaxYear())
                .totalRefundAmount(fmEntity.getTotalRefundAmount())
                .federalRefundAmount(fmEntity.getFederalRefundAmount())
                .stateRefundAmountTotal(fmEntity.getStateRefundAmountTotal())
                .irsTrackingId(fmEntity.getIrsTrackingId())
                .disbursementMethod(fmEntity.getDisbursementMethod())
                .build();

        return Optional.of(fm);
    }

    public BigDecimal getTotalRefundAmount() {
        BigDecimal fed = federalRefundAmount != null ? federalRefundAmount : BigDecimal.ZERO;
        BigDecimal st = stateRefundAmountTotal != null ? stateRefundAmountTotal : BigDecimal.ZERO;
        return fed.add(st);
    }
}
