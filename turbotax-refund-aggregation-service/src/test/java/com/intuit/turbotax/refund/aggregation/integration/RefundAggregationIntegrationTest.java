package com.intuit.turbotax.refund.aggregation.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.refund.aggregation.RefundAggregationServiceApplication;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.repository.RefundStatusRepository;

@SpringBootTest(classes = RefundAggregationServiceApplication.class)
@AutoConfigureWebMvc
class RefundAggregationIntegrationTest {

    @Autowired
    private RefundStatusRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void repositoryOperations_ShouldWorkCorrectly() {
        // Given
        RefundStatusAggregate aggregate = new RefundStatusAggregate(
                9999,
                "INTEGRATION-TEST",
                Jurisdiction.FEDERAL,
                RefundStatus.PROCESSING,
                "INT-001",
                "Integration test status",
                Instant.now(),
                BigDecimal.valueOf(500.00));

        // When
        repository.save(aggregate);
        var result = repository.findByFilingId(9999);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().filingId()).isEqualTo(9999);
        assertThat(result.get().status()).isEqualTo(RefundStatus.PROCESSING);
    }

    @Test
    void cacheManager_ShouldBeConfigured() {
        // Then
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCache("refundStatus")).isNotNull();
    }
}