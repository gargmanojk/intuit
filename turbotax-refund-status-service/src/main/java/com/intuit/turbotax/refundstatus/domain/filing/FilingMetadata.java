package com.intuit.turbotax.refundstatus.domain.filing;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilingMetadata {
    private String filingId;
    private String userId;
    private int taxYear;
    private BigDecimal federalRefundAmount;
    private BigDecimal stateRefundAmountTotal;
    private String irsTrackingId;  // tokenized
    private String disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...

    public BigDecimal getTotalRefundAmount() {
        BigDecimal fed = federalRefundAmount != null ? federalRefundAmount : BigDecimal.ZERO;
        BigDecimal st = stateRefundAmountTotal != null ? stateRefundAmountTotal : BigDecimal.ZERO;
        return fed.add(st);
    }
}
