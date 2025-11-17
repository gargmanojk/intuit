package com.intuit.turbotax.refundstatus.api;

import com.intuit.turbotax.refundstatus.dto.FilingMetadataResponse;
import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.refund.*;
import com.intuit.turbotax.refundstatus.dto.EtaPredictionResponse;
import com.intuit.turbotax.refundstatus.dto.RefundDetailsResponse;
import com.intuit.turbotax.refundstatus.dto.RefundStatusResponse;
import com.intuit.turbotax.refundstatus.integration.AiRefundEtaService;
import com.intuit.turbotax.refundstatus.dto.RefundEtaRequest;
import com.intuit.turbotax.refundstatus.dto.RefundEtaResponse;
import com.intuit.turbotax.refundstatus.integration.FilingMetadataService;
import com.intuit.turbotax.refundstatus.domain.ai.RefundEtaPrediction;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RefundStatusOrchestrator {

    private final FilingMetadataService filingMetadataService;
    private final RefundStatusAggregatorService refundStatusAggregatorService;
    private final AiRefundEtaService aiRefundEtaService;

    public RefundStatusOrchestrator(FilingMetadataService filingMetadataService,
                                    RefundStatusAggregatorService refundStatusAggregatorService,
                                    AiRefundEtaService aiRefundEtaService) {
        this.filingMetadataService = filingMetadataService;
        this.refundStatusAggregatorService = refundStatusAggregatorService;
        this.aiRefundEtaService = aiRefundEtaService;
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

    public RefundStatusResponse getLatestRefundStatus(String userId) {
        // 1. Find latest filing for this user
        Optional<FilingMetadataResponse> maybeFiling = filingMetadataService.findLatestFilingForUser(userId);

        if (maybeFiling.isEmpty()) {
            // No filing found – user hasn't filed or data not available
            return RefundStatusResponse.noFilingFound();
        }

        FilingMetadataResponse filingDto = maybeFiling.get();
        FilingMetadata filing = dtoToDomain(filingDto);

        // 2. Fetch refund statuses across jurisdictions (federal + states)
        List<RefundStatus> statuses = refundStatusAggregatorService.getRefundStatusesForFiling(filing.getFilingId());
        
        // 3. For each non-final status, ask AI for ETA
        List<RefundDetailsResponse> refundDetails = statuses.stream()
                .map(status -> {
                    EtaPredictionResponse etaDto = null;

                    if (!status.getCanonicalStatus().isFinal()) {
                        RefundEtaRequest req = RefundEtaRequest.builder()
                                .taxYear(filing.getTaxYear())
                                .filingDate(null)
                                .federalRefundAmount(filing.getFederalRefundAmount())
                                .federalDisbursementMethod(filing.getDisbursementMethod())
                                .federalReturnStatus(status.getJurisdiction() == Jurisdiction.FEDERAL ? status.getCanonicalStatus() : null)
                                .stateRefundAmount(status.getAmount())
                                .stateJurisdiction(status.getJurisdiction())
                                .stateReturnStatus(status.getJurisdiction() != Jurisdiction.FEDERAL ? status.getCanonicalStatus() : null)
                                .stateDisbursementMethod(filing.getDisbursementMethod())
                                .build();

                        java.util.Optional<RefundEtaResponse> respOpt = aiRefundEtaService.predictEta(req);
                        if (respOpt.isPresent()) {
                            RefundEtaResponse resp = respOpt.get();
                            RefundEtaPrediction prediction;
                            if (status.getJurisdiction() == Jurisdiction.FEDERAL) {
                                prediction = RefundEtaPrediction.builder()
                                        .expectedArrivalDate(resp.getFederalExpectedArrivalDate())
                                        .confidence(resp.getFederalConfidence())
                                        .windowDays(resp.getFederalWindowDays())
                                        .build();
                            } else {
                                prediction = RefundEtaPrediction.builder()
                                        .expectedArrivalDate(resp.getStateExpectedArrivalDate())
                                        .confidence(resp.getStateConfidence())
                                        .windowDays(resp.getStateWindowDays())
                                        .build();
                            }
                            etaDto = EtaPredictionResponse.fromDomain(prediction);
                        }
                    }
                    return RefundDetailsResponse.fromDomain(filing, status, etaDto);
                })
                .toList();

        return RefundStatusResponse.withRefunds(filing.getTaxYear(), refundDetails);
    }
}
