package com.intuit.turbotax.filing.query.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.intuit.turbotax.api.model.PaymentMethod;
import com.intuit.turbotax.api.model.Jurisdiction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxFilingEntity {
    private Jurisdiction jurisdiction;
    private int filingId;
    private String userId;
    private int taxYear;
    private LocalDate filingDate;
    private BigDecimal refundAmount;
    private String trackingId;  // tokenized
    private PaymentMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...

    public BigDecimal getTotalRefundAmount() {
        // For mock, just return the single refund amount field
        return refundAmount != null ? refundAmount : BigDecimal.ZERO;
    }
}
