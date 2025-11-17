package com.intuit.turbotax.aggregator.domain;

import java.util.List;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import org.springframework.stereotype.Component;
import com.intuit.turbotax.contract.Jurisdiction;
import com.intuit.turbotax.contract.RefundCanonicalStatus;

@Component
public class RefundStatusRepositoryImpl implements RefundStatusRepository {

    @Override
    public List<RefundStatus> findByFilingId(String filingId) {
        // Simple mock implementation with fixed test data
        var statuses = new ArrayList<RefundStatus>();
        
        // Federal refund status
        RefundStatus federalStatus = RefundStatus.builder()
                .filingId(filingId)
                .trackingId("IRS-TRACK-12345")
                .jurisdiction(Jurisdiction.FEDERAL)
                .status(RefundCanonicalStatus.PROCESSING)
                .rawStatusCode("FED_1001")
                .messageKey("MSG_FEDERAL_PROCESSING")
                .lastUpdatedAt(Instant.now())
                .amount(BigDecimal.valueOf(1500))
                .build();
        
        // State refund status
        RefundStatus stateStatus = RefundStatus.builder()
                .filingId(filingId)
                .trackingId("CA-TRACK-12345")
                .jurisdiction(Jurisdiction.STATE_CA)
                .status(RefundCanonicalStatus.ACCEPTED)
                .rawStatusCode("CA_2001")
                .messageKey("MSG_STATE_ACCEPTED")
                .lastUpdatedAt(Instant.now())
                .amount(BigDecimal.valueOf(250))
                .build();
        
        statuses.add(federalStatus);
        statuses.add(stateStatus);
        
        return statuses;
    }    
}