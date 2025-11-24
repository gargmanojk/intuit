package com.intuit.turbotax.filing.query.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;
import com.intuit.turbotax.filing.query.repository.TaxFilingEntity;
import com.intuit.turbotax.filing.query.repository.TaxFilingRepository;

/**
 * Configuration for initializing sample data.
 * In a production environment, this would load data from external sources.
 */
@Configuration
public class TaxFilingRepositoryConfig {

    /**
     * Initializes the repository with sample tax filing data.
     * This is executed on application startup.
     */
    @Bean
    public CommandLineRunner initializeData(TaxFilingRepository repository) {
        return args -> {
            initializeSampleData(repository);
        };
    }

    private void initializeSampleData(TaxFilingRepository repository) {
        // Federal filings
        TaxFilingEntity federal1 = createFiling(202410001, "user123", Jurisdiction.FEDERAL, 2024,
                LocalDate.of(2024, 4, 15), BigDecimal.valueOf(2500.00), "IRS-TRACK-202410001",
                PaymentMethod.ACH, true);

        TaxFilingEntity federal2 = createFiling(202410003, "user456", Jurisdiction.FEDERAL, 2024,
                LocalDate.of(2024, 3, 30), BigDecimal.valueOf(4200.75), "IRS-TRACK-202410003",
                PaymentMethod.ACH, true);

        TaxFilingEntity federal3 = createFiling(202410005, "user789", Jurisdiction.FEDERAL, 2024,
                LocalDate.of(2024, 4, 10), BigDecimal.valueOf(1850.50), "IRS-TRACK-202410005",
                PaymentMethod.CHECK, false);

        // State filings
        TaxFilingEntity state1 = createFiling(202410002, "user123", Jurisdiction.STATE_CA, 2024,
                LocalDate.of(2024, 4, 15), BigDecimal.valueOf(350.00), "CA-TRACK-202410002",
                PaymentMethod.CHECK, false);

        TaxFilingEntity state2 = createFiling(202410004, "user456", Jurisdiction.STATE_NY, 2024,
                LocalDate.of(2024, 3, 30), BigDecimal.valueOf(650.25), "NY-TRACK-202410004",
                PaymentMethod.CHECK, false);

        TaxFilingEntity state3 = createFiling(202410006, "user789", Jurisdiction.STATE_NJ, 2024,
                LocalDate.of(2024, 4, 10), BigDecimal.valueOf(0.00), "NJ-TRACK-202410006",
                PaymentMethod.CHECK, false);

        // Save all sample data
        saveFiling(repository, federal1);
        saveFiling(repository, federal2);
        saveFiling(repository, federal3);
        saveFiling(repository, state1);
        saveFiling(repository, state2);
        saveFiling(repository, state3);

        System.out.println("Initialized 6 tax filing records in memory");
    }

    private TaxFilingEntity createFiling(int filingId, String userId, Jurisdiction jurisdiction,
            int taxYear, LocalDate filingDate, BigDecimal refundAmount, String trackingId,
            PaymentMethod disbursementMethod, boolean isPaperless) {
        return TaxFilingEntity.builder()
                .filingId(filingId)
                .userId(userId)
                .jurisdiction(jurisdiction)
                .taxYear(taxYear)
                .filingDate(filingDate)
                .refundAmount(refundAmount)
                .trackingId(trackingId)
                .disbursementMethod(disbursementMethod)
                .isPaperless(isPaperless)
                .build();
    }

    private void saveFiling(TaxFilingRepository repository, TaxFilingEntity filing) {
        if (repository instanceof com.intuit.turbotax.filing.query.repository.TaxFilingRepositoryImpl impl) {
            impl.save(filing);
        }
    }
}