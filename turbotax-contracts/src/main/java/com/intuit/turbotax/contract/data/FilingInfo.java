package com.intuit.turbotax.contract.data;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilingInfo {
    private String filingId;
    private String trackingId;  // tokenized
    private Jurisdiction jurisdiction;
    private String userId;
    private int taxYear;
    private LocalDate filingDate;
    private BigDecimal refundAmount;   
    private DisbursementMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...
}