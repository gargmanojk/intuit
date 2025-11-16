package com.intuit.turbotax.refundstatus.domain.filing;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public class FilingMetadataRepositoryImpl implements FilingMetadataRepository {
    
    @Override
    public Optional<FilingMetadata> findLatestByUserId(String userId) {
        // mock data
        FilingMetadata fm = new FilingMetadata("TT2025100", userId, 2025, BigDecimal.valueOf(1000),
                BigDecimal.valueOf(100), "IRSTRKID1001", "DIRECT_DEPOSIT");
        
        return Optional.of(fm);
    }
}
