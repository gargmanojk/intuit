package com.intuit.turbotax.refundstatus.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response containing filing metadata information including refund amounts and disbursement details.
 * Generated from filing-metadata-response-schema.json
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilingMetadataResponse {
    
    @JsonProperty("filingId")
    private String filingId;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("taxYear")
    private int taxYear;
    
    @JsonProperty("totalRefundAmount")
    private BigDecimal totalRefundAmount;
    
    @JsonProperty("federalRefundAmount")
    private BigDecimal federalRefundAmount;
    
    @JsonProperty("stateRefundAmountTotal")
    private BigDecimal stateRefundAmountTotal;
    
    @JsonProperty("irsTrackingId")
    private String irsTrackingId;
    
    @JsonProperty("disbursementMethod")
    private String disbursementMethod;
}
