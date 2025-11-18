package com.intuit.turbotax.filingmetadata.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.contract.data.DisbursementMethod;
import com.intuit.turbotax.contract.data.Jurisdiction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilingMetadata {
    private Jurisdiction jurisdiction;
    private String filingId;
    private String userId;
    private int taxYear;
    private LocalDate filingDate;
    private BigDecimal refundAmount;
    private String trackingId;  // tokenized
    private DisbursementMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...

    public BigDecimal getTotalRefundAmount() {
        // For mock, just return the single refund amount field
        return refundAmount != null ? refundAmount : BigDecimal.ZERO;
    }
}
