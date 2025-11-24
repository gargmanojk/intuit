package com.intuit.turbotax.filing.query.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;
import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.filing.query.repository.TaxFilingEntity;

class TaxFilingMapperTest {

    private final TaxFilingMapper mapper = new TaxFilingMapper();

    @Test
    void entityToApi_ShouldMapEntityToApi() {
        // Given
        TaxFilingEntity entity = TaxFilingEntity.builder()
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

        // When
        TaxFiling result = mapper.entityToApi(entity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.filingId()).isEqualTo(202410001);
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.jurisdiction()).isEqualTo(Jurisdiction.FEDERAL);
        assertThat(result.taxYear()).isEqualTo(2024);
        assertThat(result.filingDate()).isEqualTo(LocalDate.of(2024, 4, 15));
        assertThat(result.refundAmount()).isEqualByComparingTo(BigDecimal.valueOf(2500.00));
        assertThat(result.trackingId()).isEqualTo("TRACK-001");
        assertThat(result.disbursementMethod()).isEqualTo(PaymentMethod.ACH);
        assertThat(result.isPaperless()).isTrue();
    }

    @Test
    void entityToApi_ShouldReturnNull_WhenEntityIsNull() {
        // When
        TaxFiling result = mapper.entityToApi(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void entityToApi_ShouldMapAllFieldsCorrectly() {
        // Given
        TaxFilingEntity entity = TaxFilingEntity.builder()
                .filingId(202410002)
                .userId("user456")
                .jurisdiction(Jurisdiction.STATE_CA)
                .taxYear(2023)
                .filingDate(LocalDate.of(2023, 3, 10))
                .refundAmount(BigDecimal.valueOf(1500.50))
                .trackingId("CA-TRACK-002")
                .disbursementMethod(PaymentMethod.CHECK)
                .isPaperless(false)
                .build();

        // When
        TaxFiling result = mapper.entityToApi(entity);

        // Then
        assertThat(result.filingId()).isEqualTo(202410002);
        assertThat(result.userId()).isEqualTo("user456");
        assertThat(result.jurisdiction()).isEqualTo(Jurisdiction.STATE_CA);
        assertThat(result.taxYear()).isEqualTo(2023);
        assertThat(result.filingDate()).isEqualTo(LocalDate.of(2023, 3, 10));
        assertThat(result.refundAmount()).isEqualByComparingTo(BigDecimal.valueOf(1500.50));
        assertThat(result.trackingId()).isEqualTo("CA-TRACK-002");
        assertThat(result.disbursementMethod()).isEqualTo(PaymentMethod.CHECK);
        assertThat(result.isPaperless()).isFalse();
    }
}