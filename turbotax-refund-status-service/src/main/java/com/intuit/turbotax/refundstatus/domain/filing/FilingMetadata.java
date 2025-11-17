package com.intuit.turbotax.refundstatus.domain.filing;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.domainmodel.DisbursementMethod;
import com.intuit.turbotax.domainmodel.Jurisdiction;

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
}
