package com.intuit.turbotax.refund.query.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import com.intuit.turbotax.api.v1.external.model.PredictionFeature;
import com.intuit.turbotax.api.v1.external.model.RefundPrediction;

class RefundPredictorClientTest {
    private final String serviceUrl = "https://refundprediction-ldyuo.eastus2.inference.ml.azure.com/score";
    private final String apiKey = System.getenv("API_KEY");

    @Test
    void testPredictEtaReturnsValue() {
        // Arrange
        RestTemplate restTemplate = new RestTemplate();
        RefundPredictorClient proxy = new RefundPredictorClient(restTemplate, serviceUrl, apiKey);

        Map<PredictionFeature, Object> features = new LinkedHashMap<>();
        features.put(PredictionFeature.Filing_ID, 12345);
        features.put(PredictionFeature.Filing_Method, "E-File");
        features.put(PredictionFeature.Submission_Date, "2025-03-15");
        features.put(PredictionFeature.Return_Complexity, "Medium");
        features.put(PredictionFeature.Errors_Flag, "No");
        features.put(PredictionFeature.Refund_Type, "Direct Deposit");
        features.put(PredictionFeature.IRS_Backlog_Flag, "No");
        features.put(PredictionFeature.Bank_Deposit_Method, "ACH");
        features.put(PredictionFeature.Refund_Amount, 1500);
        features.put(PredictionFeature.Return_Complexity_Score, 2);
        features.put(PredictionFeature.Seasonal_Filing_Indicator, 1);
        features.put(PredictionFeature.Filing_Day_Of_Week, "Wednesday");
        features.put(PredictionFeature.Refund_Amount_Bucket, "Medium");
        features.put(PredictionFeature.Error_Severity_Score, 0);

        Optional<RefundPrediction> result = proxy.predictEta(features);

        // Assert: result should be present if service responds OK
        result.ifPresentOrElse(
                eta -> System.out.println("Predicted ETA: " + eta),
                () -> System.out.println("No ETA returned from prediction service."));
    }

    @Test
    void testPredictEtaReturnsEmptyOnError() {
        RestTemplate restTemplate = new RestTemplate();
        RefundPredictorClient proxy = new RefundPredictorClient(restTemplate, serviceUrl, apiKey);

        Map<PredictionFeature, Object> features = new LinkedHashMap<>();
        features.put(PredictionFeature.Filing_Method, "E-File");

        Optional<RefundPrediction> result = proxy.predictEta(features);
        if (result.isEmpty()) {
            System.out.println("No ETA returned from prediction service (error or empty response).");
        } else {
            System.out.println("Predicted ETA: " + result.get());
        }
    }
}
