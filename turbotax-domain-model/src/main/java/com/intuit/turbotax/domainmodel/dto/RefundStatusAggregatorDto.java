package com.intuit.turbotax.domainmodel.dto;

import java.math.BigDecimal;
import java.time.Instant;

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
public class RefundStatusAggregatorDto {
    private String filingId;

    private String federalStatus;
    private RefundCanonicalStatus federalCanonicalStatus;
    private String federalRawStatusCode;
    private String federalStatusMessageKey;
    private Instant federalStatusLastUpdatedAt;
    private BigDecimal federalAmount;        
    
    private String stateStatus;
    private Jurisdiction stateJurisdiction;
    private RefundCanonicalStatus stateCanonicalStatus;
    private String stateRawStatusCode;
    private String stateStatusMessageKey;
    private Instant stateStatusLastUpdatedAt;
    private BigDecimal stateAmount;
}
