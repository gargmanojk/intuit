package com.intuit.turbotax.filingmetadata.domain;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

import com.intuit.turbotax.filingmetadata.domain.FilingMetadata;
import com.intuit.turbotax.domainmodel.Jurisdiction;
import com.intuit.turbotax.domainmodel.DisbursementMethod;


@Repository
public class FilingMetadataRepositoryImpl implements FilingMetadataRepository {
    
    @Override
    public List<FilingMetadata> findLatestByUserId(String userId) {
        // Mock data: return sample filing for any user
        FilingMetadata fmFederal = FilingMetadata.builder()
                .jurisdiction(Jurisdiction.FEDERAL)
                .filingId("TT2025100")
                .userId(userId)
                .taxYear(2024)
                .filingDate(java.time.LocalDate.of(2025, 4, 15))
                .refundAmount(BigDecimal.valueOf(2500.00))
                .trackingId("IRS-TRACK-2024-001")
                .disbursementMethod(DisbursementMethod.DIRECT_DEPOSIT)
                .build();
                
        FilingMetadata fmState = FilingMetadata.builder()
                .jurisdiction(Jurisdiction.STATE_CA)
                .filingId("TT2025101")
                .userId(userId)
                .taxYear(2024)
                .filingDate(java.time.LocalDate.of(2025, 4, 15))
                .refundAmount(BigDecimal.valueOf(350.00))
                .trackingId("STATE-TRACK-2024-001")
                .disbursementMethod(DisbursementMethod.CHECK)
                .build();
        
        return List.of(fmFederal, fmState);
    }
}
