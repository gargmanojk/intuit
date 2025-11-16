package com.intuit.turbotax.refundstatus.api;

import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadata;
import com.intuit.turbotax.refundstatus.domain.filing.FilingMetadataService;
import com.intuit.turbotax.refundstatus.domain.refund.*;
import com.intuit.turbotax.refundstatus.dto.EtaPredictionResponse;
import com.intuit.turbotax.refundstatus.dto.RefundDetailsResponse;
import com.intuit.turbotax.refundstatus.dto.RefundStatusResponse;
import com.intuit.turbotax.refundstatus.domain.ai.AiRefundEtaService;
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

    public RefundStatusResponse getLatestRefundStatus(String userId) {
        // 1. Find latest filing for this user
        Optional<FilingMetadata> maybeFiling = filingMetadataService.findLatestFilingForUser(userId);

        if (maybeFiling.isEmpty()) {
            // No filing found – user hasn't filed or data not available
            return RefundStatusResponse.noFilingFound();
        }

        FilingMetadata filing = maybeFiling.get();

        // 2. Fetch refund statuses across jurisdictions (federal + states)
        List<RefundStatus> statuses = refundStatusAggregatorService.getRefundStatusesForFiling(filing.getFilingId());

        // 3. For each non-final status, ask AI for ETA
        List<RefundDetailsResponse> refundDetails = statuses.stream()
                .map(status -> {
                    EtaPredictionResponse etaDto = null;

                    if (!status.getCanonicalStatus().isFinal()) {
                        RefundEtaPrediction prediction =
                                aiRefundEtaService.predictEta(filing, status);
                                
                        if (prediction != null) {
                            etaDto = EtaPredictionResponse.fromDomain(prediction);
                        }
                    }

                    return RefundDetailsResponse.fromDomain(filing, status, etaDto);
                })
                .toList();

        return RefundStatusResponse.withRefunds(filing.getTaxYear(), refundDetails);
    }
}
