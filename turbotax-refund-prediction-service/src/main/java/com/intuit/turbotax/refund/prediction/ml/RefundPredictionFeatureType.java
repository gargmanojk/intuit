package com.intuit.turbotax.refund.prediction.ml;

public enum RefundPredictionFeatureType {
    FILING_METHOD("filing_method"),
    FILING_DATE("filing_date"),
    REFUND_AMOUNT("refund_amount"),
    TAXPAYER_AGE("taxpayer_age"),
    FILING_STATUS("filing_status"),
    DEPENDENTS_COUNT("dependents_count"),
    STATE_FILED("state_filed");
    //...

    private final String featureName;

    RefundPredictionFeatureType(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }
}