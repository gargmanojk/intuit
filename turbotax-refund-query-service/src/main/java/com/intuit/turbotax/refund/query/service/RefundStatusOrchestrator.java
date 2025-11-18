package com.intuit.turbotax.refund.query.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.model.RefundPredictionInput;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.api.model.RefundSummary;
import com.intuit.turbotax.api.service.RefundEtaPredictor;
import com.intuit.turbotax.api.service.FilingQueryService;
import com.intuit.turbotax.api.service.RefundDataAggregator;

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

    public List<RefundSummary> getLatestRefundStatus(String userId) {
        List<RefundSummary> refundSummaries = new ArrayList<>();
        
        // 1. Find latest filing for this user
        List<TaxFiling> filings = filingQueryService.findLatestFilingForUser(userId);
        
        if (filings.isEmpty()) {
            // No filing found – user hasn't filed or data not available
            return refundSummaries;
        }
        
        TaxFiling filing = filings.get(0); // Get the latest filing
        
        // 2. Fetch refund statuses across jurisdictions (federal + states)
        List<RefundStatusData> refundInfos = refundDataAggregator.getRefundStatusesForFiling(filing.getFilingId());
        
        if (refundInfos.isEmpty()) {
            return refundSummaries;
        }
        
        // 3. For each refund status, create RefundSummary with ETA if needed
        for (RefundStatusData refundInfo : refundInfos) {
            RefundSummary.RefundSummaryBuilder builder = RefundSummary.builder()
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
                RefundPredictionInput etaRequest = RefundPredictionInput.builder()
                        .jurisdiction(refundInfo.getJurisdiction())
                        .taxYear(filing.getTaxYear())
                        .filingDate(filing.getFilingDate())
                        .refundAmount(filing.getRefundAmount())
                        .disbursementMethod(filing.getDisbursementMethod())
                        .returnStatus(refundInfo.getStatus())
                        .build();
                
                Optional<RefundEtaPrediction> etaOpt = refundEtaPredictor.predictEta(etaRequest);
                if (etaOpt.isPresent()) {
                    RefundEtaPrediction eta = etaOpt.get();
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
