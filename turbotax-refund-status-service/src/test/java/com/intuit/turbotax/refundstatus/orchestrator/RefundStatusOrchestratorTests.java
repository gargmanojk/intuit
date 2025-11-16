package com.intuit.turbotax.refundstatus.orchestrator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.turbotax.refundstatus.domain.ai.AiRefundEtaService;
import com.intuit.turbotax.refundstatus.domain.ai.RefundEtaPrediction;
import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadataService;
import com.intuit.turbotax.refundstatus.domain.refund.Jurisdiction;
import com.intuit.turbotax.refundstatus.domain.refund.RefundCanonicalStatus;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatusAggregatorService;
import com.intuit.turbotax.refundstatus.orchestrator.dto.RefundStatusResponse;

/**
 * Unit test for RefundStatusOrchestrator using mocked collaborators.
 */
public class RefundStatusOrchestratorTests {

        private FilingMetadataService filingMetadataService;
        private RefundStatusAggregatorService refundStatusAggregatorService;
        private AiRefundEtaService aiRefundEtaService;

        private RefundStatusOrchestrator orchestrator;

        @BeforeEach
        void setUp() {
                filingMetadataService = mock(FilingMetadataService.class);
                refundStatusAggregatorService = mock(RefundStatusAggregatorService.class);
                aiRefundEtaService = mock(AiRefundEtaService.class);

                orchestrator = new RefundStatusOrchestrator(
                                filingMetadataService,
                                refundStatusAggregatorService,
                                aiRefundEtaService);
        }

        @Test
        void getLatestRefundStatus_whenFilingExists_andStatusNotFinal_callsAiAndReturnsEta() {
                // Arrange
                String userId = "user-123";

                var filing = new FilingMetadata("filing-1", userId, 2024, BigDecimal.valueOf(1500.00), BigDecimal.ZERO,
                                "IRSTRKID1001", "DIRECT_DEPOSIT");

                when(filingMetadataService.findLatestFilingForUser(userId))
                                .thenReturn(Optional.of(filing));

                RefundStatus status = new RefundStatus("status-1", "filing-1", Jurisdiction.FEDERAL,
                                RefundCanonicalStatus.PROCESSING,
                                "", "", Instant.parse("2025-03-01T10:15:30Z"), BigDecimal.valueOf(1500.00));

                when(refundStatusAggregatorService.getRefundStatusesForFiling("filing-1"))
                                .thenReturn(List.of(status));

                RefundEtaPrediction prediction = new RefundEtaPrediction();
                prediction.setExpectedArrivalDate(LocalDate.of(2025, 3, 15));
                prediction.setConfidence(0.82);
                prediction.setWindowDays(3);
                prediction.setExplanationKey("IRS_EFILE_DIRECT_DEPOSIT_TYPICAL");
                prediction.setModelVersion("v1");

                when(aiRefundEtaService.predictEta(filing, status))
                                .thenReturn(prediction);

                // Act
                RefundStatusResponse response = orchestrator.getLatestRefundStatus(userId);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.isFilingFound()).isTrue();
                assertThat(response.getTaxYear()).isEqualTo(2024);
                assertThat(response.getRefunds()).hasSize(1);

                var refundDetails = response.getRefunds().get(0);
                assertThat(refundDetails.getJurisdiction()).isEqualTo(Jurisdiction.FEDERAL);
                assertThat(refundDetails.getAmount()).isEqualByComparingTo("1500.00");
                assertThat(refundDetails.getStatus()).isEqualTo(RefundCanonicalStatus.PROCESSING);
                assertThat(refundDetails.getEtaPrediction()).isNotNull();
                assertThat(refundDetails.getEtaPrediction().getExpectedArrivalDate())
                                .isEqualTo(LocalDate.of(2025, 3, 15));
                assertThat(refundDetails.getEtaPrediction().getConfidence())
                                .isEqualTo(0.82);

                // Verify interactions with mocks
                verify(filingMetadataService).findLatestFilingForUser(userId);
                verify(refundStatusAggregatorService).getRefundStatusesForFiling("filing-1");
                verify(aiRefundEtaService).predictEta(filing, status);
                verifyNoMoreInteractions(filingMetadataService,
                                refundStatusAggregatorService, aiRefundEtaService);
        }

        @Test
        void getLatestRefundStatus_whenNoFilingFound_returnsNoFilingResponse_andDoesNotCallOthers() {
                // Arrange
                String userId = "user-456";
                when(filingMetadataService.findLatestFilingForUser(userId))
                                .thenReturn(Optional.empty());

                // Act
                RefundStatusResponse response = orchestrator.getLatestRefundStatus(userId);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.isFilingFound()).isFalse();
                assertThat(response.getRefunds()).isEmpty();

                verify(filingMetadataService).findLatestFilingForUser(userId);
                verifyNoInteractions(refundStatusAggregatorService, aiRefundEtaService);
        }
}
