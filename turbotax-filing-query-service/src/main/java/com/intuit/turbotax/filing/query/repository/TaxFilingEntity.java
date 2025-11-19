package com.intuit.turbotax.filing.query.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.intuit.turbotax.api.model.PaymentMethod;
import com.intuit.turbotax.api.model.Jurisdiction;

public class TaxFilingEntity {
    private Jurisdiction jurisdiction;
    private int filingId;
    private String userId;
    private int taxYear;
    private LocalDate filingDate;
    private BigDecimal refundAmount;
    private String trackingId;  // tokenized
    private PaymentMethod disbursementMethod; // DIRECT_DEPOSIT, CARD, CHECK...

    public TaxFilingEntity() {}

    public TaxFilingEntity(Jurisdiction jurisdiction, int filingId, String userId, int taxYear,
                          LocalDate filingDate, BigDecimal refundAmount, String trackingId,
                          PaymentMethod disbursementMethod) {
        this.jurisdiction = jurisdiction;
        this.filingId = filingId;
        this.userId = userId;
        this.taxYear = taxYear;
        this.filingDate = filingDate;
        this.refundAmount = refundAmount;
        this.trackingId = trackingId;
        this.disbursementMethod = disbursementMethod;
    }

    public static TaxFilingEntityBuilder builder() {
        return new TaxFilingEntityBuilder();
    }

    // Getters
    public Jurisdiction getJurisdiction() { return jurisdiction; }
    public int getFilingId() { return filingId; }
    public String getUserId() { return userId; }
    public int getTaxYear() { return taxYear; }
    public LocalDate getFilingDate() { return filingDate; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public String getTrackingId() { return trackingId; }
    public PaymentMethod getDisbursementMethod() { return disbursementMethod; }

    // Setters
    public void setJurisdiction(Jurisdiction jurisdiction) { this.jurisdiction = jurisdiction; }
    public void setFilingId(int filingId) { this.filingId = filingId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTaxYear(int taxYear) { this.taxYear = taxYear; }
    public void setFilingDate(LocalDate filingDate) { this.filingDate = filingDate; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public void setDisbursementMethod(PaymentMethod disbursementMethod) { this.disbursementMethod = disbursementMethod; }

    public BigDecimal getTotalRefundAmount() {
        // For mock, just return the single refund amount field
        return refundAmount != null ? refundAmount : BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxFilingEntity that = (TaxFilingEntity) o;
        return filingId == that.filingId &&
               taxYear == that.taxYear &&
               jurisdiction == that.jurisdiction &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(filingDate, that.filingDate) &&
               Objects.equals(refundAmount, that.refundAmount) &&
               Objects.equals(trackingId, that.trackingId) &&
               disbursementMethod == that.disbursementMethod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jurisdiction, filingId, userId, taxYear, 
                          filingDate, refundAmount, trackingId, disbursementMethod);
    }

    @Override
    public String toString() {
        return "TaxFilingEntity{" +
               "jurisdiction=" + jurisdiction +
               ", filingId=" + filingId +
               ", userId='" + userId + '\'' +
               ", taxYear=" + taxYear +
               ", filingDate=" + filingDate +
               ", refundAmount=" + refundAmount +
               ", trackingId='" + trackingId + '\'' +
               ", disbursementMethod=" + disbursementMethod +
               '}';
    }

    public static class TaxFilingEntityBuilder {
        private Jurisdiction jurisdiction;
        private int filingId;
        private String userId;
        private int taxYear;
        private LocalDate filingDate;
        private BigDecimal refundAmount;
        private String trackingId;
        private PaymentMethod disbursementMethod;

        public TaxFilingEntityBuilder jurisdiction(Jurisdiction jurisdiction) {
            this.jurisdiction = jurisdiction;
            return this;
        }

        public TaxFilingEntityBuilder filingId(int filingId) {
            this.filingId = filingId;
            return this;
        }

        public TaxFilingEntityBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public TaxFilingEntityBuilder taxYear(int taxYear) {
            this.taxYear = taxYear;
            return this;
        }

        public TaxFilingEntityBuilder filingDate(LocalDate filingDate) {
            this.filingDate = filingDate;
            return this;
        }

        public TaxFilingEntityBuilder refundAmount(BigDecimal refundAmount) {
            this.refundAmount = refundAmount;
            return this;
        }

        public TaxFilingEntityBuilder trackingId(String trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public TaxFilingEntityBuilder disbursementMethod(PaymentMethod disbursementMethod) {
            this.disbursementMethod = disbursementMethod;
            return this;
        }

        public TaxFilingEntity build() {
            return new TaxFilingEntity(jurisdiction, filingId, userId, taxYear,
                                     filingDate, refundAmount, trackingId, disbursementMethod);
        }
    }
}
