package com.intuit.turbotax.filing.data.repository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import com.intuit.turbotax.api.model.Jurisdiction;
import com.intuit.turbotax.api.model.PaymentMethod;


@Repository
public class TaxFilingRepositoryImpl implements TaxFilingRepository {

    @Override
    public List<TaxFilingEntity> findLatestByUserId(String userId) {
        // Mock data: return sample filing for any user
        TaxFilingEntity fmFederal = TaxFilingEntity.builder()
                .jurisdiction(Jurisdiction.FEDERAL)
                .filingId(202510001)
                .userId(userId)
                .taxYear(2024)
                .filingDate(java.time.LocalDate.of(2025, 4, 15))
                .refundAmount(BigDecimal.valueOf(2500.00))
                .trackingId("IRS-TRACK-2024-001")
                .disbursementMethod(PaymentMethod.DIRECT_DEPOSIT)
                .build();
                
        TaxFilingEntity fmState = TaxFilingEntity.builder()
                .jurisdiction(Jurisdiction.STATE_CA)
                .filingId(202510002)
                .userId(userId)
                .taxYear(2024)
                .filingDate(java.time.LocalDate.of(2025, 4, 15))
                .refundAmount(BigDecimal.valueOf(350.00))
                .trackingId("STATE-TRACK-2024-001")
                .disbursementMethod(PaymentMethod.CHECK)
                .build();
        
        return List.of(fmFederal, fmState);
    }
}
