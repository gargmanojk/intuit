package com.intuit.turbotax.refund.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// ...existing code...
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;
import com.intuit.turbotax.api.v1.common.service.Cache;
import com.intuit.turbotax.api.v1.external.model.RefundPrediction;
import com.intuit.turbotax.api.v1.external.service.RefundPredictor;
import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.api.v1.filing.service.FilingQueryService;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;
import com.intuit.turbotax.api.v1.refund.model.RefundSummary;
import com.intuit.turbotax.api.v1.refund.service.RefundDataAggregator;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {
                "logging.level.com.intuit.turbotax=DEBUG",
                "spring.main.allow-bean-definition-overriding=true"
})
class RefundQueryServiceApplicationTests {

        @Autowired
        private WebTestClient webTestClient;

        @Autowired
        private FilingQueryService filingQueryService;

        @Autowired
        private RefundDataAggregator refundDataAggregator;

        @Autowired
        private RefundPredictor refundPredictor;

        @Autowired
        private Cache<List<RefundSummary>> refundSummaryCache;

        private final String TEST_USER_ID = "mock-user-id-123";

        @TestConfiguration
        static class TestConfig {
                @Bean
                @Primary
                public FilingQueryService filingQueryService() {
                        return mock(FilingQueryService.class);
                }

                @Bean
                @Primary
                public RefundDataAggregator refundDataAggregator() {
                        return mock(RefundDataAggregator.class);
                }

                @Bean
                @Primary
                public RefundPredictor refundPredictor() {
                        return mock(RefundPredictor.class);
                }

                @Bean
                @Primary
                @SuppressWarnings("unchecked")
                public Cache<List<RefundSummary>> refundSummaryCache() {
                        return mock(Cache.class);
                }
        }

        @BeforeEach
        void setUp() {
                // Reset mocks before each test
                org.mockito.Mockito.reset(filingQueryService, refundDataAggregator, refundPredictor,
                                refundSummaryCache);

                // Ensure cache always returns empty for test isolation
                when(refundSummaryCache.get(any())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Spring Context Loads Successfully")
        void contextLoads() {
                assertThat(webTestClient).isNotNull();
                assertThat(filingQueryService).isNotNull();
                assertThat(refundDataAggregator).isNotNull();
                assertThat(refundPredictor).isNotNull();
        }

        @Test
        @DisplayName("Full Integration Test - Complete Refund Query Workflow")
        // @Disabled("Test currently disabled")
        void testCompleteRefundQueryWorkflow() {
                // Arrange - Setup mock data for complete workflow
                setupMockDataForCompleteWorkflow();

                // Act & Assert - Test the complete reactive workflow
                Flux<RefundSummary> responseFlux = webTestClient.get()
                                .uri("/refund-status")
                                .header("X-User-Id", TEST_USER_ID)
                                .accept(APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType((org.springframework.http.MediaType) APPLICATION_JSON)
                                .returnResult(RefundSummary.class)
                                .getResponseBody();

                // Verify reactive stream behavior
                StepVerifier.create(responseFlux)
                                .assertNext(refundSummary -> {
                                        // Verify federal filing data
                                        assertThat(refundSummary.filingId()).isEqualTo(202501);
                                        assertThat(refundSummary.jurisdiction()).isEqualTo(Jurisdiction.FEDERAL);
                                        assertThat(refundSummary.taxYear()).isEqualTo(2024);
                                        assertThat(refundSummary.amount())
                                                        .isEqualByComparingTo(new BigDecimal("2500.00"));
                                        assertThat(refundSummary.status()).isEqualTo(RefundStatus.PROCESSING);
                                        assertThat(refundSummary.disbursementMethod()).isEqualTo(PaymentMethod.ACH);
                                        assertThat(refundSummary.etaDate()).isEqualTo(LocalDate.now().plusDays(14));
                                        assertThat(refundSummary.etaConfidence()).isEqualTo(0.85);
                                        assertThat(refundSummary.etaWindowDays()).isEqualTo(5);
                                })
                                .assertNext(refundSummary -> {
                                        // Verify state filing data
                                        assertThat(refundSummary.filingId()).isEqualTo(202502);
                                        assertThat(refundSummary.jurisdiction()).isEqualTo(Jurisdiction.STATE_CA);
                                        assertThat(refundSummary.taxYear()).isEqualTo(2024);
                                        assertThat(refundSummary.amount())
                                                        .isEqualByComparingTo(new BigDecimal("450.00"));
                                        assertThat(refundSummary.status()).isEqualTo(RefundStatus.SENT_TO_BANK);
                                        assertThat(refundSummary.disbursementMethod()).isEqualTo(PaymentMethod.CHECK);
                                        assertThat(refundSummary.etaDate()).isEqualTo(LocalDate.now().plusDays(7));
                                        assertThat(refundSummary.etaConfidence()).isEqualTo(0.92);
                                        assertThat(refundSummary.etaWindowDays()).isEqualTo(3);
                                })
                                .verifyComplete();

                // Verify service interactions
                verify(filingQueryService, times(1)).getFilings(eq(TEST_USER_ID));
                verify(refundDataAggregator, times(2)).getRefundStatusForFiling(anyInt());
                verify(refundPredictor, times(2)).predictEta(any());
        }

        @Test
        @DisplayName("Integration Test - No Filing Data Found")
        void testNoFilingDataFound() {
                // Arrange - No filing data
                when(filingQueryService.getFilings(TEST_USER_ID))
                                .thenReturn(List.of());

                // Act & Assert
                Flux<RefundSummary> responseFlux = webTestClient.get()
                                .uri("/refund-status")
                                .header("X-User-Id", TEST_USER_ID)
                                .accept(APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .returnResult(RefundSummary.class)
                                .getResponseBody();

                StepVerifier.create(responseFlux)
                                .verifyComplete(); // Empty flux

                verify(filingQueryService, times(1)).getFilings(eq(TEST_USER_ID));
                verify(refundDataAggregator, times(0)).getRefundStatusForFiling(anyInt());
                verify(refundPredictor, times(0)).predictEta(any());
        }

        @Test
        @DisplayName("Integration Test - Partial Data Scenario")
        void testPartialDataScenario() {
                // Arrange - Filing exists but no status or ETA data
                TaxFiling federalFiling = createMockFiling(202501, Jurisdiction.FEDERAL,
                                new BigDecimal("1200.00"), PaymentMethod.ACH, true);

                when(filingQueryService.getFilings(TEST_USER_ID))
                                .thenReturn(List.of(federalFiling));
                when(refundDataAggregator.getRefundStatusForFiling(202501))
                                .thenReturn(Optional.empty()); // No status data
                when(refundPredictor.predictEta(any()))
                                .thenReturn(Optional.empty()); // No ETA data

                // Act & Assert
                Flux<RefundSummary> responseFlux = webTestClient.get()
                                .uri("/refund-status")
                                .header("X-User-Id", TEST_USER_ID)
                                .accept(APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .returnResult(RefundSummary.class)
                                .getResponseBody();

                StepVerifier.create(responseFlux)
                                .verifyComplete(); // Empty flux since no refund status data found

                verify(filingQueryService, times(1)).getFilings(eq(TEST_USER_ID));
                verify(refundDataAggregator, times(1)).getRefundStatusForFiling(eq(202501));
                verify(refundPredictor, times(0)).predictEta(any()); // Not called when no status data
        }

        @Test
        @DisplayName("Integration Test - Error Handling for Service Failures")
        void testServiceFailureHandling() {
                // Arrange - Service throws exception
                when(filingQueryService.getFilings(TEST_USER_ID))
                                .thenThrow(new RuntimeException("External service unavailable"));

                // Act & Assert - Should handle gracefully
                webTestClient.get()
                                .uri("/refund-status")
                                .header("X-User-Id", TEST_USER_ID)
                                .accept(APPLICATION_JSON)
                                .exchange()
                                .expectStatus().is5xxServerError();

                verify(filingQueryService, times(1)).getFilings(eq(TEST_USER_ID));
        }

        @Test
        @DisplayName("Performance Test - Multiple Concurrent Requests")
        void testConcurrentRequests() {
                // Arrange
                setupMockDataForCompleteWorkflow();

                // Act & Assert - Multiple concurrent requests
                for (int i = 0; i < 5; i++) {
                        webTestClient.get()
                                        .uri("/refund-status")
                                        .header("X-User-Id", TEST_USER_ID)
                                        .accept(APPLICATION_JSON)
                                        .exchange()
                                        .expectStatus().isOk()
                                        .expectHeader()
                                        .contentType((org.springframework.http.MediaType) APPLICATION_JSON);
                }

                // Verify all requests were handled
                verify(filingQueryService, times(5)).getFilings(eq(TEST_USER_ID));
        }

        // Helper Methods

        private void setupMockDataForCompleteWorkflow() {
                // Mock filing data
                TaxFiling federalFiling = createMockFiling(202501, Jurisdiction.FEDERAL,
                                new BigDecimal("2500.00"), PaymentMethod.ACH, true);
                TaxFiling stateFiling = createMockFiling(202502, Jurisdiction.STATE_CA,
                                new BigDecimal("450.00"), PaymentMethod.CHECK, false);

                when(filingQueryService.getFilings(TEST_USER_ID))
                                .thenReturn(List.of(federalFiling, stateFiling));

                // Mock refund status data
                RefundStatusData federalStatus = new RefundStatusData(202501, RefundStatus.PROCESSING,
                                Jurisdiction.FEDERAL, Instant.now());
                RefundStatusData stateStatus = new RefundStatusData(202502, RefundStatus.SENT_TO_BANK,
                                Jurisdiction.STATE_CA, Instant.now());

                when(refundDataAggregator.getRefundStatusForFiling(202501))
                                .thenReturn(Optional.of(federalStatus));
                when(refundDataAggregator.getRefundStatusForFiling(202502))
                                .thenReturn(Optional.of(stateStatus));

                // Mock ETA prediction data
                when(refundPredictor.predictEta(any()))
                                .thenAnswer(invocation -> {
                                        Map<com.intuit.turbotax.api.v1.external.model.PredictionFeature, Object> features = invocation
                                                        .getArgument(0);
                                        Integer filingId = (Integer) features
                                                        .get(com.intuit.turbotax.api.v1.external.model.PredictionFeature.Filing_ID);
                                        if (filingId == 202501) {
                                                return Optional.of(new RefundPrediction(LocalDate.now().plusDays(14),
                                                                0.85, 5));
                                        } else {
                                                return Optional.of(new RefundPrediction(LocalDate.now().plusDays(7),
                                                                0.92, 3));
                                        }
                                });
        }

        private TaxFiling createMockFiling(int filingId, Jurisdiction jurisdiction,
                        BigDecimal refundAmount, PaymentMethod paymentMethod, boolean isPaperless) {
                return new TaxFiling(
                                filingId,
                                "TRACK-" + filingId,
                                jurisdiction,
                                TEST_USER_ID,
                                2024,
                                LocalDate.now().minusDays(30),
                                refundAmount,
                                paymentMethod,
                                isPaperless);
        }
}
