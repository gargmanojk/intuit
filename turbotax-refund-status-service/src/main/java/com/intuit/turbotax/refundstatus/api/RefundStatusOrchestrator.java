package com.intuit.turbotax.refundstatus.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.intuit.turbotax.contract.data.EtaRefundInfo;
import com.intuit.turbotax.contract.data.AiFeatures;
import com.intuit.turbotax.contract.data.FilingInfo;
import com.intuit.turbotax.contract.data.RefundInfo;
import com.intuit.turbotax.contract.data.RefundSummaryInfo;
import com.intuit.turbotax.contract.service.RefundEtaPredictor;
import com.intuit.turbotax.contract.service.FilingQueryService;
import com.intuit.turbotax.contract.service.RefundDataAggregator;

@Service
public class RefundStatusOrchestrator {

    private final FilingQueryService filingQueryService;
    private final RefundDataAggregator refundDataAggregator;
    private final RefundEtaPredictor refundEtaPredictor;

    public RefundStatusOrchestrator(FilingQueryService filingQueryService,
                                    RefundDataAggregator refundDataAggregator,
                                    RefundEtaPredictor refundEtaPredictor) {
        this.filingQueryService = filingQueryService;
        this.refundDataAggregator = refundDataAggregator;
        this.refundEtaPredictor = refundEtaPredictor;
    }

    public List<RefundSummaryInfo> getLatestRefundStatus(String userId) {
        List<RefundSummaryInfo> refundSummaries = new ArrayList<>();
        
        // 1. Find latest filing for this user
        List<FilingInfo> filings = filingQueryService.findLatestFilingForUser(userId);
        
        if (filings.isEmpty()) {
            // No filing found – user hasn't filed or data not available
            return refundSummaries;
        }
        
        FilingInfo filing = filings.get(0); // Get the latest filing
        
        // 2. Fetch refund statuses across jurisdictions (federal + states)
        List<RefundInfo> refundInfos = refundDataAggregator.getRefundStatusesForFiling(filing.getFilingId());
        
        if (refundInfos.isEmpty()) {
            return refundSummaries;
        }
        
        // 3. For each refund status, create RefundSummaryInfo with ETA if needed
        for (RefundInfo refundInfo : refundInfos) {
            RefundSummaryInfo.RefundSummaryInfoBuilder builder = RefundSummaryInfo.builder()
                    .filingId(filing.getFilingId())
                    .trackingId(filing.getTrackingId())
                    .taxYear(filing.getTaxYear())
                    .filingDate(filing.getFilingDate())
                    .jurisdiction(refundInfo.getJurisdiction())
                    .amount(filing.getRefundAmount())
                    .status(refundInfo.getStatus())
                    .disbursementMethod(filing.getDisbursementMethod())
                    .lastUpdatedAt(refundInfo.getLastUpdatedAt());
            
            // Get ETA prediction if status is not final
            if (refundInfo.getStatus() != null && !refundInfo.getStatus().isFinal()) {
                AiFeatures etaRequest = AiFeatures.builder()
                        .jurisdiction(refundInfo.getJurisdiction())
                        .taxYear(filing.getTaxYear())
                        .filingDate(filing.getFilingDate())
                        .refundAmount(filing.getRefundAmount())
                        .disbursementMethod(filing.getDisbursementMethod())
                        .returnStatus(refundInfo.getStatus())
                        .build();
                
                Optional<EtaRefundInfo> etaOpt = refundEtaPredictor.predictEta(etaRequest);
                if (etaOpt.isPresent()) {
                    EtaRefundInfo eta = etaOpt.get();
                    builder.etaDate(eta.getExpectedArrivalDate())
                           .etaConfidence(eta.getConfidence())
                           .etaWindowDays(eta.getWindowDays());
                }
            }
            
            refundSummaries.add(builder.build());
        }
        
        return refundSummaries;
    }
}
