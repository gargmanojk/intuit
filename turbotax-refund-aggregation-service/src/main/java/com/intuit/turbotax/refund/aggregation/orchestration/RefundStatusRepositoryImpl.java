package com.intuit.turbotax.refund.aggregation.orchestration;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import org.springframework.stereotype.Component;
import com.intuit.turbotax.api.model.Jurisdiction;

@Component
public class RefundStatusRepositoryImpl implements RefundStatusRepository {

    @Override
    public Optional<RefundStatusAggregate> findByFilingId(int filingId) {
        // Validate filing ID format (should be 9 digits starting with 2024)
        String filingIdStr = String.valueOf(filingId);
        if (filingIdStr.length() != 9 || !filingIdStr.startsWith("2024")) {
            return Optional.empty();
        }
        
        // Check if it's an even number (federal) or odd number (state)
        // This matches the pattern from TaxFilingRepositoryImpl
        boolean isFederal = (filingId % 2 == 0);
        
        if (isFederal) {
            // Federal refund status (even filing IDs)
            RefundStatusAggregate federalResult = new RefundStatusAggregate(
                filingId,
                "IRS-TRACK-" + filingId,
                Jurisdiction.FEDERAL,
                com.intuit.turbotax.api.model.RefundStatus.PROCESSING,
                "FED_" + filingId,
                "Your federal tax refund is being processed by the IRS",
                Instant.now(),
                BigDecimal.valueOf(2500)
            );
            return Optional.of(federalResult);
        } else {
           // State refund status (odd filing IDs)
            RefundStatusAggregate stateResult = new RefundStatusAggregate(
                filingId,
                "CA-TRACK-" + filingId,
                Jurisdiction.STATE_CA,
                com.intuit.turbotax.api.model.RefundStatus.ACCEPTED,
                "CA_" + filingId,
                "Your California state tax refund has been accepted",
                Instant.now(),
                BigDecimal.valueOf(350)
            );
            return Optional.of(stateResult);
        }
    }
}