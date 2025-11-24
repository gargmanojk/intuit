package com.intuit.turbotax.filing.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;
import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.filing.query.exception.InvalidUserException;
import com.intuit.turbotax.filing.query.mapper.TaxFilingMapper;
import com.intuit.turbotax.filing.query.repository.TaxFilingEntity;
import com.intuit.turbotax.filing.query.repository.TaxFilingRepository;
import com.intuit.turbotax.filing.query.validation.FilingValidator;

@ExtendWith(MockitoExtension.class)
class FilingQueryServiceImplTest {

    @Mock
    private TaxFilingRepository repository;

    @Mock
    private TaxFilingMapper mapper;

    @Mock
    private FilingValidator validator;

    @InjectMocks
    private FilingQueryServiceImpl service;

    @Test
    void getFilings_ShouldReturnFilings() {
        // Given
        String userId = "user123";
        TaxFilingEntity entity = createTestEntity();
        TaxFiling filing = createTestFiling();

        when(repository.findLatestByUserId(userId)).thenReturn(Stream.of(entity));
        when(mapper.entityToApi(entity)).thenReturn(filing);

        // When
        List<TaxFiling> result = service.getFilings(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(filing);
    }

    @Test
    void getFilings_ShouldThrowException_WhenUserIdInvalid() {
        // Given
        String invalidUserId = "";
        doThrow(new InvalidUserException("Invalid user ID")).when(validator).validateUserId(invalidUserId);

        // When & Then
        assertThatThrownBy(() -> service.getFilings(invalidUserId))
                .isInstanceOf(InvalidUserException.class);
    }

    private TaxFilingEntity createTestEntity() {
        return TaxFilingEntity.builder()
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
    }

    private TaxFiling createTestFiling() {
        return new TaxFiling(
                202410001, "TRACK-001", Jurisdiction.FEDERAL, "user123",
                2024, LocalDate.of(2024, 4, 15), BigDecimal.valueOf(2500.00),
                PaymentMethod.ACH, true);
    }
}