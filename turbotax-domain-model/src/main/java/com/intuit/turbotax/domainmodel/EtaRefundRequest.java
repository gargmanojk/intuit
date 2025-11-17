package com.intuit.turbotax.domainmodel;

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
public class EtaRefundRequest {
    private int taxYear;    
    private Jurisdiction jurisdiction; 
    private LocalDate  filingDate;
    private BigDecimal refundAmount;
    private RefundCanonicalStatus returnStatus;    
    private DisbursementMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...     
}
