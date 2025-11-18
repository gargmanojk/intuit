package com.intuit.turbotax.refund.prediction.ml;

/**
 * Enumeration of features used in refund prediction models.
 * Each feature represents a data point that contributes to predicting
 * refund processing time and likelihood of issues.
 */
public enum RefundPredictionFeatureType {
    
    // Filing Characteristics
    FILING_METHOD("filing_method", "Method used to file return (e-file, paper, etc.)"),
    FILING_DATE("filing_date", "Date when return was filed"),
    FILING_COMPLEXITY("filing_complexity", "Complexity score of the tax return"),
    RETURN_TYPE("return_type", "Type of tax return (1040, 1040EZ, etc.)"),
    
    // Financial Features
    REFUND_AMOUNT("refund_amount", "Total refund amount claimed"),
    ADJUSTED_GROSS_INCOME("agi", "Adjusted Gross Income"),
    TOTAL_TAX("total_tax", "Total tax liability"),
    WITHHOLDING_AMOUNT("withholding", "Total tax withholding"),
    
    // Credits and Deductions
    EARNED_INCOME_CREDIT("eic", "Earned Income Credit claimed"),
    CHILD_TAX_CREDIT("ctc", "Child Tax Credit claimed"),
    ADDITIONAL_CHILD_TAX_CREDIT("actc", "Additional Child Tax Credit claimed"),
    EDUCATION_CREDITS("education_credits", "Education credits claimed"),
    ITEMIZED_DEDUCTIONS("itemized_deductions", "Itemized deductions claimed"),
    
    // Taxpayer Demographics
    TAXPAYER_AGE("taxpayer_age", "Primary taxpayer age"),
    FILING_STATUS("filing_status", "Filing status (single, married, etc.)"),
    DEPENDENTS_COUNT("dependents_count", "Number of dependents claimed"),
    STATE_FILED("state_filed", "State where return was filed"),
    
    // Historical Patterns
    PRIOR_YEAR_REFUND("prior_year_refund", "Previous year refund amount"),
    PRIOR_YEAR_PROCESSING_TIME("prior_year_processing_time", "Previous year processing time"),
    TAXPAYER_HISTORY_SCORE("taxpayer_history_score", "Historical compliance score"),
    
    // Risk Indicators
    IDENTITY_VERIFICATION_REQUIRED("identity_verification", "Identity verification flag"),
    MATH_ERROR_PROBABILITY("math_error_prob", "Probability of math errors"),
    DOCUMENT_VERIFICATION_REQUIRED("document_verification", "Document verification flag"),
    FRAUD_RISK_SCORE("fraud_risk_score", "Fraud risk assessment score"),
    
    // Processing Factors
    IRS_PROCESSING_WORKLOAD("irs_workload", "Current IRS processing workload"),
    SEASONAL_FACTOR("seasonal_factor", "Seasonal processing factor"),
    SYSTEM_CAPACITY("system_capacity", "Processing system capacity"),
    
    // External Factors
    BANK_ROUTING_VALIDATION("bank_routing", "Bank routing number validation"),
    DIRECT_DEPOSIT_TYPE("direct_deposit_type", "Type of direct deposit account"),
    REFUND_DELIVERY_METHOD("refund_delivery_method", "Method of refund delivery");
    
    private final String featureName;
    private final String description;
    
    RefundPredictionFeatureType(String featureName, String description) {
        this.featureName = featureName;
        this.description = description;
    }
    
    /**
     * Gets the feature name used in ML models
     * @return the feature name
     */
    public String getFeatureName() {
        return featureName;
    }
    
    /**
     * Gets the human-readable description of the feature
     * @return the feature description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Finds a feature type by its feature name
     * @param featureName the feature name to search for
     * @return the matching feature type, or null if not found
     */
    public static RefundPredictionFeatureType fromFeatureName(String featureName) {
        for (RefundPredictionFeatureType type : values()) {
            if (type.featureName.equals(featureName)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * Checks if a feature is related to taxpayer demographics
     * @return true if this is a demographic feature
     */
    public boolean isDemographicFeature() {
        return this == TAXPAYER_AGE || this == FILING_STATUS || 
               this == DEPENDENTS_COUNT || this == STATE_FILED;
    }
    
    /**
     * Checks if a feature is related to financial amounts
     * @return true if this is a financial feature
     */
    public boolean isFinancialFeature() {
        return this == REFUND_AMOUNT || this == ADJUSTED_GROSS_INCOME || 
               this == TOTAL_TAX || this == WITHHOLDING_AMOUNT;
    }
    
    /**
     * Checks if a feature is related to risk assessment
     * @return true if this is a risk feature
     */
    public boolean isRiskFeature() {
        return this == IDENTITY_VERIFICATION_REQUIRED || this == MATH_ERROR_PROBABILITY ||
               this == DOCUMENT_VERIFICATION_REQUIRED || this == FRAUD_RISK_SCORE;
    }
    
    /**
     * Checks if a feature is related to historical patterns
     * @return true if this is a historical feature
     */
    public boolean isHistoricalFeature() {
        return this == PRIOR_YEAR_REFUND || this == PRIOR_YEAR_PROCESSING_TIME ||
               this == TAXPAYER_HISTORY_SCORE;
    }
}