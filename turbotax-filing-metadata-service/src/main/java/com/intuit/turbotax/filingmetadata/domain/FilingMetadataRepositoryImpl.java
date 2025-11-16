package com.intuit.turbotax.filingmetadata.domain;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public class FilingMetadataRepositoryImpl implements FilingMetadataRepository {
    
    @Override
    public Optional<FilingMetadata> findLatestByUserId(String userId) {
        // mock data
        FilingMetadata fm = FilingMetadata.builder()
                .filingId("TT2025100")
                .userId(userId)
                .taxYear(2025)
                .federalRefundAmount(BigDecimal.valueOf(1000))
                .stateRefundAmountTotal(BigDecimal.valueOf(100))
                .irsTrackingId("IRSTRKID1001")
                .disbursementMethod("DIRECT_DEPOSIT")
                .build();
        
        return Optional.of(fm);
    }
}
