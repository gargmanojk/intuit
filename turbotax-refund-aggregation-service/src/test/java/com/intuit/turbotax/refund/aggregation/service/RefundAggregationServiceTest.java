package com.intuit.turbotax.refund.aggregation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;
import com.intuit.turbotax.refund.aggregation.exception.InvalidFilingIdException;
import com.intuit.turbotax.refund.aggregation.mapper.RefundStatusMapper;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;
import com.intuit.turbotax.refund.aggregation.validation.RefundAggregationValidator;

@ExtendWith(MockitoExtension.class)
class RefundAggregationServiceTest {

    @Mock
    private RefundStatusRepository repository;

    @Mock
    private RefundStatusMapper mapper;

    @Mock
    private RefundAggregationValidator validator;

    @InjectMocks
    private RefundAggregationService service;

    @Test
    void getRefundStatusForFiling_ShouldReturnStatus_WhenFound() {
        // Given
        int filingId = 12345;
        RefundStatusAggregate aggregate = createTestAggregate(filingId);
        RefundStatusData expectedData = createTestRefundStatusData(filingId);

        when(repository.findByFilingId(filingId)).thenReturn(Optional.of(aggregate));
        when(mapper.mapToApi(filingId, aggregate)).thenReturn(expectedData);

        // When
        Optional<RefundStatusData> result = service.getRefundStatusForFiling(filingId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().filingId()).isEqualTo(filingId);
    }

    @Test
    void getRefundStatusForFiling_ShouldReturnEmpty_WhenNotFound() {
        // Given
        int filingId = 99999;
        when(repository.findByFilingId(filingId)).thenReturn(Optional.empty());

        // When
        Optional<RefundStatusData> result = service.getRefundStatusForFiling(filingId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getRefundStatusForFiling_ShouldValidateFilingId() {
        // Given
        int invalidFilingId = -1;
        doThrow(new InvalidFilingIdException("Invalid filing ID"))
                .when(validator).validateFilingId(invalidFilingId);

        // When & Then
        assertThatThrownBy(() -> service.getRefundStatusForFiling(invalidFilingId))
                .isInstanceOf(InvalidFilingIdException.class);
    }

    private RefundStatusAggregate createTestAggregate(int filingId) {
        return new RefundStatusAggregate(
                filingId,
                "TRACK-" + filingId,
                Jurisdiction.FEDERAL,
                RefundStatus.PROCESSING,
                "RAW-001",
                "Your refund is being processed",
                Instant.now(),
                BigDecimal.valueOf(1500.00));
    }

    private RefundStatusData createTestRefundStatusData(int filingId) {
        return new RefundStatusData(
                filingId,
                RefundStatus.PROCESSING,
                Jurisdiction.FEDERAL,
                Instant.now());
    }
}