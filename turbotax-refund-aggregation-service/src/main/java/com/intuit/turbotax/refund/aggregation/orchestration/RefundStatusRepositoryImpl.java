package com.intuit.turbotax.refund.aggregation.orchestration;

import java.util.List;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import org.springframework.stereotype.Component;
import com.intuit.turbotax.api.model.Jurisdiction;

@Component
public class RefundStatusRepositoryImpl implements RefundStatusRepository {

    @Override
    public List<RefundStatusAggregate> findByFilingId(int filingId) {
        // Simple mock implementation with fixed test data
        var statuses = new ArrayList<RefundStatusAggregate>();
        
        // Federal refund status
        RefundStatusAggregate federalStatus = new RefundStatusAggregate(
                filingId,
                "IRS-TRACK-12345",
                Jurisdiction.FEDERAL,
                com.intuit.turbotax.api.model.RefundStatus.PROCESSING,
                "FED_1001",
                "MSG_FEDERAL_PROCESSING",
                Instant.now(),
                BigDecimal.valueOf(1500)
        );
        
        // State refund status
        RefundStatusAggregate stateStatus = new RefundStatusAggregate(
                filingId,
                "CA-TRACK-12345",
                Jurisdiction.STATE_CA,
                com.intuit.turbotax.api.model.RefundStatus.ACCEPTED,
                "CA_2001",
                "MSG_STATE_ACCEPTED",
                Instant.now(),
                BigDecimal.valueOf(250)
        );
        
        statuses.add(federalStatus);
        statuses.add(stateStatus);
        
        return statuses;
    }    
}