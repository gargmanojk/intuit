package com.intuit.turbotax.domainmodel.dto;

import java.math.BigDecimal;
import java.util.Optional;

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
public class FilingMetadataResponse {
    private String filingId;
    private Jurisdiction jurisdiction;
    private String userId;
    private int taxYear;
    private BigDecimal refundAmount;   
    private String trackingId;  // tokenized
    private DisbursementMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...
}
