package com.intuit.turbotax.refundstatus.orchestrator.dto;

import java.util.Collections;
import java.util.List;

public class RefundStatusResponse {

    private boolean filingFound;
    private Integer taxYear;
    private List<RefundDetailsResponse> refunds;

    public RefundStatusResponse() {

    }

    private RefundStatusResponse(boolean filingFound, Integer taxYear, List<RefundDetailsResponse> refunds) {
        this.filingFound = filingFound;
        this.taxYear = taxYear;
        this.refunds = refunds;
    }

    public boolean isFilingFound() {
        return filingFound;
    }

    public void setFilingFound(boolean filingFound) {
        this.filingFound = filingFound;
    }

    public Integer getTaxYear() {
        return taxYear;
    }

    public void setTaxYear(Integer taxYear) {
        this.taxYear = taxYear;
    }

    public List<RefundDetailsResponse> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<RefundDetailsResponse> refunds) {
        this.refunds = refunds;
    }   

    public static RefundStatusResponse noFilingFound() {
        return new RefundStatusResponse(false, null, Collections.emptyList());
    }

    public static RefundStatusResponse withRefunds(int taxYear, List<RefundDetailsResponse> refunds) {
        return new RefundStatusResponse(true, taxYear, refunds);
    }
}

