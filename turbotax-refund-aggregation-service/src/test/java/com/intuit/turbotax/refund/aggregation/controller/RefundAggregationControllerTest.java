package com.intuit.turbotax.refund.aggregation.controller;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;
import com.intuit.turbotax.refund.aggregation.service.RefundAggregationServiceAdapter;

@WebFluxTest(RefundAggregationController.class)
class RefundAggregationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RefundAggregationServiceAdapter refundAggregationServiceAdapter;

    @Test
    void getRefundStatusForFiling_ShouldReturnStatus_WhenFound() {
        // Given
        int filingId = 12345;
        RefundStatusData expectedData = new RefundStatusData(
                filingId,
                RefundStatus.PROCESSING,
                Jurisdiction.FEDERAL,
                Instant.now());

        when(refundAggregationServiceAdapter.getRefundStatusForFiling(filingId))
                .thenReturn(Optional.of(expectedData));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/aggregate-status/filings/{filingId}", filingId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.filingId").isEqualTo(filingId)
                .jsonPath("$.status").isEqualTo("PROCESSING")
                .jsonPath("$.jurisdiction").isEqualTo("FEDERAL");
    }

    @Test
    void getRefundStatusForFiling_ShouldReturnNotFound_WhenNotFound() {
        // Given
        int filingId = 99999;
        when(refundAggregationServiceAdapter.getRefundStatusForFiling(filingId))
                .thenReturn(Optional.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/aggregate-status/filings/{filingId}", filingId)
                .exchange()
                .expectStatus().isNotFound();
    }
}