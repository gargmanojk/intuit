package com.intuit.turbotax.filing.query.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;

class TaxFilingRepositoryImplTest {

    private TaxFilingRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new TaxFilingRepositoryImpl();
        // Add test data
        addTestData();
    }

    @Test
    void findLatestByUserId_ShouldReturnFilingsForUser() {
        // When
        List<TaxFilingEntity> filings = repository.findLatestByUserId("user123")
                .collect(Collectors.toList());

        // Then
        assertThat(filings).hasSize(2);
        assertThat(filings).allMatch(filing -> "user123".equals(filing.getUserId()));
    }

    @Test
    void findLatestByUserId_ShouldReturnEmptyForUnknownUser() {
        // When
        List<TaxFilingEntity> filings = repository.findLatestByUserId("unknown")
                .collect(Collectors.toList());

        // Then
        assertThat(filings).isEmpty();
    }

    @Test
    void save_ShouldAddFilingToRepository() {
        // Given
        TaxFilingEntity newFiling = TaxFilingEntity.builder()
                .filingId(202410007)
                .userId("user999")
                .jurisdiction(Jurisdiction.FEDERAL)
                .taxYear(2024)
                .filingDate(LocalDate.of(2024, 4, 20))
                .refundAmount(BigDecimal.valueOf(1000.00))
                .trackingId("TRACK-007")
                .disbursementMethod(PaymentMethod.ACH)
                .isPaperless(true)
                .build();

        // When
        repository.save(newFiling);
        List<TaxFilingEntity> userFilings = repository.findLatestByUserId("user999")
                .collect(Collectors.toList());

        // Then
        assertThat(userFilings).hasSize(1);
        assertThat(userFilings.get(0).getUserId()).isEqualTo("user999");
        assertThat(userFilings.get(0).getFilingId()).isEqualTo(202410007);
    }

    private void addTestData() {
        TaxFilingEntity federal = TaxFilingEntity.builder()
                .filingId(202410001)
                .userId("user123")
                .jurisdiction(Jurisdiction.FEDERAL)
                .taxYear(2024)
                .filingDate(LocalDate.of(2024, 4, 15))
                .refundAmount(BigDecimal.valueOf(2500.00))
                .trackingId("TRACK-001")
                .disbursementMethod(PaymentMethod.ACH)
                .isPaperless(true)
                .build();

        TaxFilingEntity state = TaxFilingEntity.builder()
                .filingId(202410002)
                .userId("user123")
                .jurisdiction(Jurisdiction.STATE_CA)
                .taxYear(2024)
                .filingDate(LocalDate.of(2024, 4, 15))
                .refundAmount(BigDecimal.valueOf(350.00))
                .trackingId("TRACK-002")
                .disbursementMethod(PaymentMethod.CHECK)
                .isPaperless(false)
                .build();

        repository.save(federal);
        repository.save(state);
    }
}