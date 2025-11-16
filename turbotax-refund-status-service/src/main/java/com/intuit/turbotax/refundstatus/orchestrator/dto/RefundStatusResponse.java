package com.intuit.turbotax.refundstatus.orchestrator.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class RefundStatusResponse {

    private boolean filingFound;
    private Integer taxYear;
    private List<RefundDetailsResponse> refunds;

    public RefundStatusResponse() {}

    private RefundStatusResponse(boolean filingFound, Integer taxYear,
                                 List<RefundDetailsResponse> refunds) {
        this.filingFound = filingFound;
        this.taxYear = taxYear;
        this.refunds = refunds;
    }

    public static RefundStatusResponse noFilingFound() {
        return new RefundStatusResponse(false, null, Collections.emptyList());
    }

    public static RefundStatusResponse withRefunds(int taxYear, List<RefundDetailsResponse> refunds) {
        return new RefundStatusResponse(true, taxYear, refunds);
    }

    // getters/setters omitted for brevity
}

