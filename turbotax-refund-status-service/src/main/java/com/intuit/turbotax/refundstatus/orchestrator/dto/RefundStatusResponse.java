package com.intuit.turbotax.refundstatus.orchestrator.dto;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundStatusResponse {
    private boolean filingFound;
    private Integer taxYear;
    private List<RefundDetailsResponse> refunds;
    
    public static RefundStatusResponse noFilingFound() {
        return new RefundStatusResponse(false, null, Collections.emptyList());
    }

    public static RefundStatusResponse withRefunds(int taxYear, List<RefundDetailsResponse> refunds) {
        return new RefundStatusResponse(true, taxYear, refunds);
    }
}

