package com.intuit.turbotax.refundstatus.domain.filing;

import java.math.BigDecimal;

public class FilingMetadata {

    private String filingId;
    private String userId;
    private int taxYear;
    private BigDecimal federalRefundAmount;
    private BigDecimal stateRefundAmountTotal;
    private String irsTrackingId;  // tokenized
    private String disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...

    public FilingMetadata() {
        
    }

    public FilingMetadata(String filingId, String userId, int taxYear, BigDecimal federalRefundAmount,
            BigDecimal stateRefundAmountTotal, String irsTrackingId, String disbursementMethod) {
        this.filingId = filingId;
        this.userId = userId;
        this.taxYear = taxYear;
        this.federalRefundAmount = federalRefundAmount;
        this.stateRefundAmountTotal = stateRefundAmountTotal;
        this.irsTrackingId = irsTrackingId;
        this.disbursementMethod = disbursementMethod;
    }   

    public String getFilingId() {
        return filingId;
    }

    public void setFilingId(String filingId) {
        this.filingId = filingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTaxYear() {
        return taxYear;
    }

    public void setTaxYear(int taxYear) {
        this.taxYear = taxYear;
    }

    public BigDecimal getFederalRefundAmount() {
        return federalRefundAmount;
    }

    public void setFederalRefundAmount(BigDecimal federalRefundAmount) {
        this.federalRefundAmount = federalRefundAmount;
    }

    public BigDecimal getStateRefundAmountTotal() {
        return stateRefundAmountTotal;
    }

    public void setStateRefundAmountTotal(BigDecimal stateRefundAmountTotal) {
        this.stateRefundAmountTotal = stateRefundAmountTotal;
    }

    public String getIrsTrackingId() {
        return irsTrackingId;
    }

    public void setIrsTrackingId(String irsTrackingId) {
        this.irsTrackingId = irsTrackingId;
    }

    public String getDisbursementMethod() {
        return disbursementMethod;
    }

    public void setDisbursementMethod(String disbursementMethod) {
        this.disbursementMethod = disbursementMethod;
    }

    public BigDecimal getTotalRefundAmount() {
        BigDecimal fed = federalRefundAmount != null ? federalRefundAmount : BigDecimal.ZERO;
        BigDecimal st = stateRefundAmountTotal != null ? stateRefundAmountTotal : BigDecimal.ZERO;
        return fed.add(st);
    }
}
