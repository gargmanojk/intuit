package com.intuit.turbotax.aieta.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import com.intuit.turbotax.aieta.domain.EtaFeature;
import com.intuit.turbotax.aieta.domain.ModelOutput;

@Service
public class ModelInferenceServiceImpl implements ModelInferenceService {

    @Override
    public ModelOutput predict(List<EtaFeature> features) {
        // Simple mock inference logic:
        // - Look for numeric features (federalRefundAmount / stateRefundAmount) and daysFromFiling
        // - Adjust a baseline expected days by disbursement method and amount
        // - Produce a confidence score based on amount and recency
        double amount = 0.0;
        double daysFromFiling = 0.0;
        String disbursementMethod = null;

        if (features != null) {
            for (EtaFeature f : features) {
                if (f == null || f.getName() == null || f.getValue() == null) continue;
                String name = f.getName();
                String value = f.getValue();
                try {
                    switch (name) {
                        case "federalRefundAmount":
                        case "stateRefundAmount":
                            // take the first amount we see (mock behaviour)
                            if (amount == 0.0) {
                                amount = Double.parseDouble(value);
                            }
                            break;
                        case "daysFromFiling":
                            daysFromFiling = Double.parseDouble(value);
                            break;
                        case "federalDisbursementMethod":
                        case "stateDisbursementMethod":
                            if (disbursementMethod == null) disbursementMethod = value;
                            break;
                        default:
                            // ignore other features in the mock
                    }
                } catch (NumberFormatException e) {
                    // ignore poorly formed numeric features
                }
            }
        }

        double expectedDays = 14.0;
        // Faster for direct deposit
        if (disbursementMethod != null && disbursementMethod.equalsIgnoreCase("DIRECT_DEPOSIT")) {
            expectedDays -= 5.0;
        }

        // Larger refunds may be processed faster in this mock
        expectedDays -= Math.min(5.0, amount / 1000.0);

        // Older filings (more days since filing) increase expected days slightly
        expectedDays += Math.min(7.0, daysFromFiling / 30.0);

        if (expectedDays < 1.0) expectedDays = 1.0;

        double confidence = 0.6 + Math.min(0.35, amount / 5000.0);
        if (daysFromFiling > 60) confidence -= 0.1;
        if (confidence < 0.0) confidence = 0.0;
        if (confidence > 1.0) confidence = 1.0;

        return ModelOutput.builder()
                .expectedDays(expectedDays)
                .confidence(confidence)
                .modelVersion("mock-v1")
                .build();
    }
}
