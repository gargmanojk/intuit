package com.intuit.turbotax.contract;

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
public class AiFeatures {
    private int taxYear;    
    private Jurisdiction jurisdiction; 
    private LocalDate  filingDate;
    private BigDecimal refundAmount;
    private RefundStatus returnStatus;    
    private DisbursementMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...    
    //... add more features as needed
}
