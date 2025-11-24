package com.intuit.turbotax.refund.aggregation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;

class RefundStatusRepositoryImplTest {

    private RefundStatusRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new RefundStatusRepositoryImpl();
        // Add test data
        addTestData();
    }

    @Test
    void findByFilingId_ShouldReturnAggregate_WhenFound() {
        // When
        Optional<RefundStatusAggregate> result = repository.findByFilingId(1001);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().filingId()).isEqualTo(1001);
        assertThat(result.get().jurisdiction()).isEqualTo(Jurisdiction.FEDERAL);
    }

    @Test
    void findByFilingId_ShouldReturnEmpty_WhenNotFound() {
        // When
        Optional<RefundStatusAggregate> result = repository.findByFilingId(9999);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getActiveFilingIds_ShouldReturnOnlyNonFinalStatuses() {
        // When
        List<Integer> activeIds = repository.getActiveFilingIds()
                .collect(Collectors.toList());

        // Then
        assertThat(activeIds).hasSize(3); // Only test data: 1001, 1002, 1004 (1003 is DEPOSITED/final)
        assertThat(activeIds).contains(1001, 1002, 1004);
    }

    @Test
    void save_ShouldAddNewAggregate() {
        // Given
        RefundStatusAggregate newAggregate = createTestAggregate(2000, RefundStatus.FILED);

        // When
        repository.save(newAggregate);
        Optional<RefundStatusAggregate> result = repository.findByFilingId(2000);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().status()).isEqualTo(RefundStatus.FILED);
    }

    @Test
    void save_ShouldUpdateExistingAggregate() {
        // Given
        RefundStatusAggregate updated = createTestAggregate(1001, RefundStatus.DEPOSITED);

        // When
        repository.save(updated);
        Optional<RefundStatusAggregate> result = repository.findByFilingId(1001);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().status()).isEqualTo(RefundStatus.DEPOSITED);
    }

    private void addTestData() {
        // Active statuses (not final)
        repository.save(createTestAggregate(1001, RefundStatus.PROCESSING));
        repository.save(createTestAggregate(1002, RefundStatus.ACCEPTED));

        // Final statuses (should not be returned by getActiveFilingIds)
        repository.save(createTestAggregate(1003, RefundStatus.DEPOSITED));
        repository.save(createTestAggregate(1004, RefundStatus.ERROR));
    }

    private RefundStatusAggregate createTestAggregate(int filingId, RefundStatus status) {
        return new RefundStatusAggregate(
                filingId,
                "TRACK-" + filingId,
                Jurisdiction.FEDERAL,
                status,
                "RAW-" + filingId,
                "Test message for " + filingId,
                Instant.now(),
                BigDecimal.valueOf(1000.00 + filingId));
    }
}