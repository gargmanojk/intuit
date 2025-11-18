package com.intuit.turbotax.aieta.domain;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ModelInferenceServiceImpl implements ModelInferenceService {

    @Override
    public ModelOutput predict(List<EtaFeature> features) {
        // Simple mock: baseline 14 days, adjusted by disbursement method and amount
        double amount = 0.0;
        double daysFromFiling = 0.0;
        String disbursementMethod = null;

        if (features != null) {
            for (EtaFeature f : features) {
                if (f == null || f.getName() == null || f.getValue() == null) continue;
                try {
                    switch (f.getName()) {
                        case "refundAmount":                        
                            if (amount == 0.0) amount = Double.parseDouble(f.getValue());
                            break;
                        case "daysFromFiling":
                            daysFromFiling = Double.parseDouble(f.getValue());
                            break;
                        case "disbursementMethod":                       
                            if (disbursementMethod == null) disbursementMethod = f.getValue();
                            break;
                    }
                } catch (NumberFormatException e) {
                    // Ignore malformed values
                }
            }
        }

        double expectedDays = 14.0;
        
        if ("DIRECT_DEPOSIT".equalsIgnoreCase(disbursementMethod)) {
            expectedDays -= 5.0;
        }
        
        expectedDays -= Math.min(5.0, amount / 1000.0);
        expectedDays += Math.min(7.0, daysFromFiling / 30.0);
        
        expectedDays = Math.max(1.0, expectedDays);

        double confidence = 0.6 + Math.min(0.35, amount / 5000.0);
        if (daysFromFiling > 60) confidence -= 0.1;
        confidence = Math.max(0.0, Math.min(1.0, confidence));

        return ModelOutput.builder()
                .expectedDays(expectedDays)
                .confidence(confidence)
                .modelVersion("mock-v1")
                .build();
    }
}
