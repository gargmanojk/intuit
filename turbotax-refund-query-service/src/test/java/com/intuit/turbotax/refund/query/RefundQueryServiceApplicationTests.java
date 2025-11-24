package com.intuit.turbotax.refund.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.intuit.turbotax.api.v1.common.model.Jurisdiction;
import com.intuit.turbotax.api.v1.common.model.PaymentMethod;
import com.intuit.turbotax.api.v1.external.model.RefundPrediction;
import com.intuit.turbotax.api.v1.external.service.RefundPredictor;
import com.intuit.turbotax.api.v1.filing.model.TaxFiling;
import com.intuit.turbotax.api.v1.filing.service.FilingQueryService;
import com.intuit.turbotax.api.v1.refund.model.RefundStatus;
import com.intuit.turbotax.api.v1.refund.model.RefundStatusData;
import com.intuit.turbotax.api.v1.refund.model.RefundSummary;
import com.intuit.turbotax.api.v1.refund.service.RefundDataAggregator;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {
                "logging.level.com.intuit.turbotax=DEBUG",
                "spring.main.allow-bean-definition-overriding=true",
                "spring.cache.type=none" // Disable caching for tests
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RefundQueryServiceApplicationTests {

        @Autowired
        private WebTestClient webTestClient;

        @MockBean
        private FilingQueryService filingQueryService;

        @MockBean
        private RefundDataAggregator refundDataAggregator;

        @MockBean
        private RefundPredictor refundPredictor;

        @BeforeEach
        void setUp() {
                // Reset mocks before each test
                org.mockito.Mockito.reset(filingQueryService, refundDataAggregator, refundPredictor);
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
                setupMockDataForCompleteWorkflow("test-user-1");

                // Act & Assert - Test the complete workflow
                webTestClient.get()
                                .uri("/api/v1/refund-status")
                                .header("X-User-Id", "test-user-1")
                                .accept(APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk();

                // Verify service interactions
                verify(filingQueryService, times(1)).getFilings(eq("test-user-1"));
                verify(refundDataAggregator, times(2)).getRefundStatusForFiling(anyInt());
                verify(refundPredictor, times(2)).predictEta(any());
        }

        @Test
        @DisplayName("Integration Test - No Filing Data Found")
        void testNoFilingDataFound() {
                // Arrange - No filing data
                when(filingQueryService.getFilings("test-user-2"))
                                .thenReturn(List.of());

                // Act & Assert
                webTestClient.get()
                                .uri("/api/v1/refund-status")
                                .header("X-User-Id", "test-user-2")
                                .accept(APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBodyList(RefundSummary.class)
                                .hasSize(0); // Empty list

                verify(filingQueryService, times(1)).getFilings(eq("test-user-2"));
                verify(refundDataAggregator, times(0)).getRefundStatusForFiling(anyInt());
                verify(refundPredictor, times(0)).predictEta(any());
        }

        @Test
        @DisplayName("Integration Test - Partial Data Scenario")
        void testPartialDataScenario() {
                // Arrange - Filing exists but no status or ETA data
                TaxFiling federalFiling = createMockFiling(202501, Jurisdiction.FEDERAL,
                                new BigDecimal("1200.00"), PaymentMethod.ACH, "test-user-3");

                when(filingQueryService.getFilings("test-user-3"))
                                .thenReturn(List.of(federalFiling));
                when(refundDataAggregator.getRefundStatusForFiling(202501))
                                .thenReturn(Optional.empty()); // No status data
                when(refundPredictor.predictEta(any()))
                                .thenReturn(Optional.empty()); // No ETA data

                // Act & Assert
                webTestClient.get()
                                .uri("/api/v1/refund-status")
                                .header("X-User-Id", "test-user-3")
                                .accept(APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBodyList(RefundSummary.class)
                                .hasSize(0); // Empty list since no refund status data found

                verify(filingQueryService, times(1)).getFilings(eq("test-user-3"));
                verify(refundDataAggregator, times(1)).getRefundStatusForFiling(eq(202501));
                verify(refundPredictor, times(0)).predictEta(any()); // Not called when no status data
        }

        @Test
        @DisplayName("Integration Test - Error Handling for Service Failures")
        void testServiceFailureHandling() {
                // Arrange - Service throws exception
                when(filingQueryService.getFilings("test-user-4"))
                                .thenThrow(new RuntimeException("External service unavailable"));

                // Act & Assert - Should handle gracefully
                webTestClient.get()
                                .uri("/api/v1/refund-status")
                                .header("X-User-Id", "test-user-4")
                                .accept(APPLICATION_JSON)
                                .exchange()
                                .expectStatus().is5xxServerError();

                verify(filingQueryService, times(1)).getFilings(eq("test-user-4"));
        }

        @Test
        @DisplayName("Performance Test - Multiple Concurrent Requests")
        void testConcurrentRequests() {
                // Arrange
                setupMockDataForCompleteWorkflow("test-user-5");

                // Act & Assert - Multiple concurrent requests
                for (int i = 0; i < 5; i++) {
                        webTestClient.get()
                                        .uri("/api/v1/refund-status")
                                        .header("X-User-Id", "test-user-5")
                                        .accept(APPLICATION_JSON)
                                        .exchange()
                                        .expectStatus().isOk();
                }

                // Verify all requests were handled (cached, so only 1 call to external service)
                verify(filingQueryService, times(1)).getFilings(eq("test-user-5"));
        }

        // Helper Methods

        private void setupMockDataForCompleteWorkflow(String userId) {
                // Mock filing data
                TaxFiling federalFiling = createMockFiling(202501, Jurisdiction.FEDERAL,
                                new BigDecimal("2500.00"), PaymentMethod.ACH, userId);
                TaxFiling stateFiling = createMockFiling(202502, Jurisdiction.STATE_CA,
                                new BigDecimal("450.00"), PaymentMethod.CHECK, userId);

                when(filingQueryService.getFilings(userId))
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
                        BigDecimal refundAmount, PaymentMethod paymentMethod, String userId) {
                return new TaxFiling(
                                filingId,
                                "TRACK-" + filingId,
                                jurisdiction,
                                userId,
                                2024,
                                LocalDate.now().minusDays(30),
                                refundAmount,
                                paymentMethod,
                                true);
        }
}
