package com.intuit.turbotax.refundstatus.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.intuit.turbotax.refundstatus.domain.refund.Jurisdiction;
import com.intuit.turbotax.refundstatus.domain.refund.RefundCanonicalStatus;

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
