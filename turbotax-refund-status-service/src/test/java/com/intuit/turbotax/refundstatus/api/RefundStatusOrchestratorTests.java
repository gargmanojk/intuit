package com.intuit.turbotax.refundstatus.api;

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
import com.intuit.turbotax.refundstatus.dto.FilingMetadataResponse;
import com.intuit.turbotax.refundstatus.integration.FilingMetadataService;
import com.intuit.turbotax.refundstatus.domain.refund.Jurisdiction;
import com.intuit.turbotax.refundstatus.domain.refund.RefundCanonicalStatus;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatusAggregatorService;
import com.intuit.turbotax.refundstatus.dto.RefundStatusResponse;

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
        var filingDto = FilingMetadataResponse
                .builder()
                .filingId("filing-1")
                .taxYear(2024)
                .federalRefundAmount(BigDecimal.valueOf(1500.00))
                .stateRefundAmountTotal(BigDecimal.ZERO)
                .irsTrackingId("IRSTRK1001")
                .disbursementMethod("DIRECT_DEPOSIT")
                .build();
        
        when(filingMetadataService.findLatestFilingForUser(userId))
                .thenReturn(Optional.of(filingDto));

        var status = RefundStatus
                .builder()
                .statusId("status-1")
                .filingId("filing-1")
                .jurisdiction(Jurisdiction.FEDERAL)
                .canonicalStatus(RefundCanonicalStatus.PROCESSING)
                .statusLastUpdatedAt(Instant.parse("2025-03-01T10:15:30Z"))
                .amount(BigDecimal.valueOf(1500.00))
                .build();
        when(refundStatusAggregatorService.getRefundStatusesForFiling("filing-1"))
                .thenReturn(List.of(status));

        var filing = dtoToDomain(filingDto);
        var prediction = RefundEtaPrediction
                .builder()
                .expectedArrivalDate(LocalDate.of(2025, 3, 15))
                .confidence(0.82)
                .windowDays(3)
                .explanationKey("IRS_EFILE_DIRECT_DEPOSIT_TYPICAL")
                .modelVersion("v1")
                .build();
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

    /**
     * Convert FilingMetadataResponse (DTO) to FilingMetadata (domain object).
     */
    private FilingMetadata dtoToDomain(FilingMetadataResponse dto) {
        return FilingMetadata.builder()
                .filingId(dto.getFilingId())
                .userId(dto.getUserId())
                .taxYear(dto.getTaxYear())
                .federalRefundAmount(dto.getFederalRefundAmount())
                .stateRefundAmountTotal(dto.getStateRefundAmountTotal())
                .irsTrackingId(dto.getIrsTrackingId())
                .disbursementMethod(dto.getDisbursementMethod())
                .build();
    }
}
