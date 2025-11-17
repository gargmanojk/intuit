package com.intuit.turbotax.contract;

import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundSummaryInfo {
    private String filingId;
    private String trackingId;  // tokenized
    private Integer taxYear;
    private LocalDate filingDate;
    private Jurisdiction jurisdiction;
    private BigDecimal amount;
    private RefundCanonicalStatus status;
    private DisbursementMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...
    private Instant lastUpdatedAt;
    //eta prediction details
    private LocalDate etaDate;
    private double etaConfidence;
    private int etaWindowDays;
}