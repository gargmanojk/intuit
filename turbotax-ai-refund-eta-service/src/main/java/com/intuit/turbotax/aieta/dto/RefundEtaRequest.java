package com.intuit.turbotax.aieta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.domainmodel.Jurisdiction;
import com.intuit.turbotax.domainmodel.RefundCanonicalStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundEtaRequest {
    private int taxYear;    
    private LocalDate  filingDate;

    private BigDecimal federalRefundAmount;
    private RefundCanonicalStatus federalReturnStatus; 
    private String federalDisbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...
    
    private BigDecimal stateRefundAmount;
    private Jurisdiction stateJurisdiction;
    private RefundCanonicalStatus stateReturnStatus; 
    private String stateDisbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...
}
