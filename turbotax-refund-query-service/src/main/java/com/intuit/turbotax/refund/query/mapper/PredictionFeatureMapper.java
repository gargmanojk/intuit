package com.intuit.turbotax.refund.query.mapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.intuit.turbotax.api.v1.common.model.PaymentMethod;
import com.intuit.turbotax.api.v1.external.model.PredictionFeature;
import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;

public class PredictionFeatureMapper {
    public static Map<PredictionFeature, Object> mapToFeatures(RefundStatusData refundInfo, TaxFiling filing) {
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

    private static String getFilingDate(TaxFiling filing) {
        return filing.filingDate() != null ? filing.filingDate().toString() : null;
    }

    private static String getReturnComplexity(TaxFiling filing) {
        String[] complexities = { "Low", "Medium", "High" };
        return complexities[Math.abs(filing.filingId()) % complexities.length];
    }

    private static String getIrsBacklog(TaxFiling filing) {
        String[] backlogs = { "Yes", "No" };
        return backlogs[filing.filingId() % backlogs.length];
    }

    private static String getRefundType(TaxFiling filing) {
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

    private static String getBankDepositMethod(TaxFiling filing) {
        PaymentMethod method = filing.disbursementMethod();
        switch (method) {
            case WIRE:
            case ACH:
                return "Wire";
            default:
                return "N/A";
        }
    }

    private static String getFilingDayOfWeek(TaxFiling filing) {
        String day = filing.filingDate().getDayOfWeek().toString().toLowerCase();
        return day.substring(0, 1).toUpperCase() + day.substring(1);
    }

    private static String getRefundAmountBucket(TaxFiling filing) {
        BigDecimal amount = filing.refundAmount();
        if (amount.compareTo(BigDecimal.valueOf(1000)) < 0)
            return "Small";
        else if (amount.compareTo(BigDecimal.valueOf(2500)) <= 0)
            return "Medium";
        else
            return "Large";
    }

    private static int getReturnComplexityScore(TaxFiling filing) {
        return (filing.filingId() % 3) + 1; // Score between 1 and 3
    }

    private static int getErrorSeverityScore(RefundStatusData refundInfo) {
        return refundInfo.status() == RefundStatus.ERROR ? 1 : 0;
    }

    private static String getErrorsFlag(RefundStatusData refundInfo) {
        return (refundInfo.status() == RefundStatus.ERROR) ? "Yes" : "No";
    }

    private static int getSeasonalFilingIndicator(TaxFiling filing) {
        return (filing.filingDate().getMonthValue() >= 2 && filing.filingDate().getMonthValue() <= 4) ? 1 : 0;
    }

    private static String getFilingMethod(TaxFiling filing) {
        return (filing.isPaperless() ? "E-File" : "Paper");
    }
}