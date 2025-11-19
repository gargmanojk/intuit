package com.intuit.turbotax.filing.query.repository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.time.LocalDate;
import com.intuit.turbotax.api.model.Jurisdiction;
import com.intuit.turbotax.api.model.PaymentMethod;


@Repository
public class TaxFilingRepositoryImpl implements TaxFilingRepository {
    
    private final Random random = new Random();

    @Override
    public List<TaxFilingEntity> findLatestByUserId(String userId) {
        // Generate different filing IDs for federal and state
        int federalFilingId = randomFilingId(true);  // Even number for federal
        int stateFilingId = randomFilingId(false);   // Odd number for state
        
        // Mock data: return sample filing for any user
        TaxFilingEntity fmFederal = TaxFilingEntity.builder()
            .jurisdiction(Jurisdiction.FEDERAL)
            .filingId(federalFilingId)
            .userId(userId)
            .taxYear(2024)
            .filingDate(LocalDate.of(2025, 4, 15))
            .refundAmount(BigDecimal.valueOf(2500.00))
            .trackingId("IRS-TRACK-" + federalFilingId)
            .disbursementMethod(PaymentMethod.DIRECT_DEPOSIT)
            .build();
            
        TaxFilingEntity fmState = TaxFilingEntity.builder()
            .jurisdiction(Jurisdiction.STATE_CA)
            .filingId(stateFilingId)
            .userId(userId)
            .taxYear(2024)
            .filingDate(LocalDate.of(2025, 4, 15))
            .refundAmount(BigDecimal.valueOf(350.00))
            .trackingId("CA-TRACK-" + stateFilingId)
            .disbursementMethod(PaymentMethod.CHECK)
            .build();
        
        return List.of(fmFederal, fmState);
    }

    @Override
    public Optional<TaxFilingEntity> findByFilingId(int filingId) {
        // Dynamic mock data based on filing ID patterns
        String filingIdStr = String.valueOf(filingId);
        if (filingIdStr.length() != 9 || !filingIdStr.startsWith("2024")) {
            return Optional.empty();
        }        
        
        // Check if it's an even number (federal) or odd number (state)
        boolean isFederal = (filingId % 2 == 0);
        
        TaxFilingEntity.TaxFilingEntityBuilder builder = TaxFilingEntity.builder()
                .filingId(filingId)
                .taxYear(2024)
                .filingDate(LocalDate.of(2025, 4, 15));
        
        if (isFederal) {
            // Federal filing (even filing ID)
            builder.jurisdiction(Jurisdiction.FEDERAL)
                   .refundAmount(BigDecimal.valueOf(2500.00))
                   .trackingId("IRS-TRACK-" + filingId)
                   .disbursementMethod(PaymentMethod.DIRECT_DEPOSIT);
        } else {
            // State filing (odd filing ID)
            builder.jurisdiction(Jurisdiction.STATE_CA)
                   .refundAmount(BigDecimal.valueOf(350.00))
                   .trackingId("CA-TRACK-" + filingId)
                   .disbursementMethod(PaymentMethod.CHECK);
        }
        
        return Optional.of(builder.build());
    }

    /**
     * Generates a random filing ID in the format YYYYNNNNN
     * where YYYY is 2024 and NNNNN is a 5-digit random number
     * @param federal true for even numbers (federal), false for odd numbers (state)
     */
    private int randomFilingId(boolean federal) {
        int year = 2024;
        int randomPart;
        
        if (federal) {
            // Generate even 5-digit number for federal filings
            randomPart = 10000 + (random.nextInt(45000) * 2); // Even numbers 10000-99998
        } else {
            // Generate odd 5-digit number for state filings  
            randomPart = 10001 + (random.nextInt(44999) * 2); // Odd numbers 10001-99999
        }
        
        return year * 100000 + randomPart; // Combines year + random part
    }
}
