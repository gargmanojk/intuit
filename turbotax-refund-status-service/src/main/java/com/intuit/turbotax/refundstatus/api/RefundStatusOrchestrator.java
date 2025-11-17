package com.intuit.turbotax.refundstatus.api;

import com.intuit.turbotax.refundstatus.dto.FilingMetadataResponse;
import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.refund.RefundStatus;
import com.intuit.turbotax.refundstatus.integration.RefundStatusAggregatorService;
import com.intuit.turbotax.domainmodel.Jurisdiction;
import com.intuit.turbotax.domainmodel.RefundCanonicalStatus;
import com.intuit.turbotax.refundstatus.dto.EtaPredictionResponse;
import com.intuit.turbotax.refundstatus.dto.RefundDetailsResponse;
import com.intuit.turbotax.refundstatus.dto.RefundStatusResponse;
import com.intuit.turbotax.refundstatus.dto.RefundStatusAggregatorResponse;
import com.intuit.turbotax.refundstatus.integration.AiRefundEtaService;
import com.intuit.turbotax.refundstatus.dto.RefundEtaRequest;
import com.intuit.turbotax.refundstatus.dto.RefundEtaResponse;
import com.intuit.turbotax.refundstatus.integration.FilingMetadataService;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

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
        FilingMetadata domain = new FilingMetadata();
        domain.setFilingId(dto.getFilingId());  
        domain.setUserId(dto.getUserId());
        domain.setTaxYear(dto.getTaxYear());
        domain.setFilingDate(dto.getFilingDate());
        domain.setRefundAmount(dto.getRefundAmount());
        domain.setDisbursementMethod(dto.getDisbursementMethod());
        domain.setJurisdiction(dto.getJurisdiction());

        return domain;
    }

    public List<RefundStatusResponse> getLatestRefundStatus(String userId) {
        // 1. Find latest filing for this user
        List<FilingMetadataResponse> filings = filingMetadataService.findLatestFilingForUser(userId);

        if (filings.isEmpty()) {
            // No filing found – user hasn't filed or data not available
            return List.of();
        }

        for (FilingMetadataResponse filingMetadataResponse : filings) {
            
        }

        FilingMetadataResponse filingDto = filings.get(0);  
        FilingMetadata filing = dtoToDomain(filingDto);

        // 2. Fetch refund statuses across jurisdictions (federal + states)
        Optional<RefundStatusAggregatorResponse> aggResp = refundStatusAggregatorService.getRefundStatusesForFiling(filing.getFilingId());
        
        if (aggResp.isEmpty()) {
            return RefundStatusResponse.noFilingFound();
        }

        // Convert aggregator response to domain RefundStatus objects
        List<RefundStatus> statuses = aggregatorResponseToStatuses(aggResp.get());
        
        // 3. For each non-final status, ask AI for ETA
        List<RefundDetailsResponse> refundDetails = statuses.stream()
                .map(status -> {
                    EtaPredictionResponse etaDto = null;

                    if (!status.getCanonicalStatus().isFinal()) {
                        RefundEtaRequest req = RefundEtaRequest.builder()
                                .jurisdiction(status.getJurisdiction())
                                .taxYear(filing.getTaxYear())
                                .filingDate(filing.getFilingDate())
                                .refundAmount(filing.getRefundAmount())
                                .disbursementMethod(filing.getDisbursementMethod())
                                .returnStatus(status.getCanonicalStatus())
                                .build();

                        java.util.Optional<RefundEtaResponse> respOpt = aiRefundEtaService.predictEta(req);
                        if (respOpt.isPresent()) {
                            RefundEtaResponse resp = respOpt.get();
                            if (status.getJurisdiction() == Jurisdiction.FEDERAL) {
                                etaDto = EtaPredictionResponse.builder()
                                        .expectedArrivalDate(resp.getExpectedArrivalDate())
                                        .confidence(resp.getConfidence())
                                        .windowDays(resp.getWindowDays())
                                        .build();
                            } else {
                                etaDto = EtaPredictionResponse.builder()
                                        .expectedArrivalDate(resp.getExpectedArrivalDate())
                                        .confidence(resp.getConfidence())
                                        .windowDays(resp.getWindowDays())
                                        .build();   
                            }
                        }
                    }
                    return RefundDetailsResponse.fromDomain(filing, status, etaDto);
                })
                .toList();

        return RefundStatusResponse.withRefunds(filing.getTaxYear(), refundDetails);
    }

    /**
     * Convert RefundStatusAggregatorResponse to a list of RefundStatus domain objects.
     */
    private List<RefundStatus> aggregatorResponseToStatuses(RefundStatusAggregatorResponse aggResp) {
        List<RefundStatus> statuses = new ArrayList<>();
        
        // Federal status
        if (aggResp.getFederalStatus() != null) {
            RefundStatus federal = RefundStatus.builder()
                    .statusId(aggResp.getFederalStatus())
                    .filingId(aggResp.getFilingId())
                    .jurisdiction(Jurisdiction.FEDERAL)
                    .canonicalStatus(aggResp.getFederalCanonicalStatus())
                    .rawStatusCode(aggResp.getFederalRawStatusCode())
                    .statusMessageKey(aggResp.getFederalStatusMessageKey())
                    .statusLastUpdatedAt(aggResp.getFederalStatusLastUpdatedAt())
                    .amount(aggResp.getFederalAmount())
                    .build();
            statuses.add(federal);
        }
        
        // State status
        if (aggResp.getStateStatus() != null) {
            RefundStatus state = RefundStatus.builder()
                    .statusId(aggResp.getStateStatus())
                    .filingId(aggResp.getFilingId())
                    .jurisdiction(aggResp.getStateJurisdiction())
                    .canonicalStatus(aggResp.getStateCanonicalStatus())
                    .rawStatusCode(aggResp.getStateRawStatusCode())
                    .statusMessageKey(aggResp.getStateStatusMessageKey())
                    .statusLastUpdatedAt(aggResp.getStateStatusLastUpdatedAt())
                    .amount(aggResp.getStateAmount())
                    .build();
            statuses.add(state);
        }
        
        return statuses;
    }
}
