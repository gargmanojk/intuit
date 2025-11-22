package com.intuit.turbotax.refund.query.service;

import com.intuit.turbotax.api.model.PaymentMethod;
import com.intuit.turbotax.api.model.PredictionFeature;
import com.intuit.turbotax.api.model.RefundStatus;
import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.api.model.TaxFiling;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RefundFeatureMapper {
    public Map<PredictionFeature, Object> mapToFeatures(RefundStatusData refundInfo, TaxFiling filing) {
        Map<PredictionFeature, Object> features = new HashMap<>();
        features.put(PredictionFeature.Filing_ID, filing.filingId());
        features.put(PredictionFeature.Filing_Method, getFilingMethod(filing));
        features.put(PredictionFeature.Submission_Date, getFilingDate(filing));
        features.put(PredictionFeature.Return_Complexity, getReturnComplexity(filing));
        features.put(PredictionFeature.Errors_Flag, getErrorsFlag(refundInfo));
        features.put(PredictionFeature.Refund_Type, getRefundType(filing));
        features.put(PredictionFeature.IRS_Backlog_Flag, getIrsBacklog(filing));
        features.put(PredictionFeature.Bank_Deposit_Method, getBankDepositMethod(filing));
        features.put(PredictionFeature.Refund_Amount, filing.refundAmount());
        features.put(PredictionFeature.Return_Complexity_Score, getReturnComplexityScore(filing));
        features.put(PredictionFeature.Seasonal_Filing_Indicator, getSeasonalFilingIndicator(filing));
        features.put(PredictionFeature.Filing_Day_Of_Week, getFilingDayOfWeek(filing));
        features.put(PredictionFeature.Refund_Amount_Bucket, getRefundAmountBucket(filing));
        features.put(PredictionFeature.Error_Severity_Score, getErrorSeverityScore(refundInfo));        

        return features;
    }
    
    private String getFilingDate(TaxFiling filing) {
        return filing.filingDate() != null ? filing.filingDate().toString() : null;
    }

    private static String getReturnComplexity(TaxFiling filing) {
        String[] complexities = {"Low", "Medium", "High"};
        return complexities[Math.abs(filing.filingId()) % complexities.length];
    }

    private String getIrsBacklog(TaxFiling filing) {
        String[] backlogs = {"Yes", "No"};
        return backlogs[filing.filingId() % backlogs.length];
    }

    private String getRefundType(TaxFiling filing) {
        PaymentMethod method = filing.disbursementMethod();
        switch (method) {
            case WIRE, ACH:
                return "Direct Deposit";
            case CHECK:
                return "Check";
            default:
                return "N/A";
        }
    }

    private String getBankDepositMethod(TaxFiling filing) {
        PaymentMethod method = filing.disbursementMethod();
        switch (method) {
            case WIRE:
                return "Wire";
            case ACH:
                return "ACH";
            default:
                return "N/A";
        }
    }

    private String getFilingDayOfWeek(TaxFiling filing) {
        String day = filing.filingDate().getDayOfWeek().toString().toLowerCase();
        return day.substring(0, 1).toUpperCase() + day.substring(1);
    }

    private String getRefundAmountBucket(TaxFiling filing) {
        BigDecimal amount = filing.refundAmount();
        if (amount.compareTo(BigDecimal.valueOf(500)) < 0) return "Small";
        else if (amount.compareTo(BigDecimal.valueOf(2000)) < 0) return "Medium";
        else return "Large";
    }   

    private int getReturnComplexityScore(TaxFiling filing) {
        return (filing.filingId() % 3) + 1; // Score between 1 and 3
    }

    private int getErrorSeverityScore(RefundStatusData refundInfo) {
        return refundInfo.status() == RefundStatus.ERROR ? 1 : 0;    
    }

    private String getErrorsFlag(RefundStatusData refundInfo) {
        return (refundInfo.status() == RefundStatus.ERROR) ? "Yes" : "No";        
    }

    private int getSeasonalFilingIndicator(TaxFiling filing) {
        return (filing.filingId() % 2 == 0) ? 0 : 1;
    }

    private String getFilingMethod(TaxFiling filing) {
        return (filing.isPaperless() ? "E-File" : "Paper");
    }
}