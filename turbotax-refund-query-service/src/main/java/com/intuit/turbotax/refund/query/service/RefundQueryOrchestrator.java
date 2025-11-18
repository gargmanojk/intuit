package com.intuit.turbotax.refund.query.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.api.model.RefundSummary;
import com.intuit.turbotax.api.service.RefundEtaPredictor;
import com.intuit.turbotax.api.service.FilingQueryService;
import com.intuit.turbotax.api.service.RefundDataAggregator;

@Service
public class RefundQueryOrchestrator {

    private final FilingQueryService filingQueryService;
    private final RefundDataAggregator refundDataAggregator;
    private final RefundEtaPredictor refundEtaPredictor;

    public RefundQueryOrchestrator(FilingQueryService filingQueryService,
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
        List<RefundStatusData> refundInfos = refundDataAggregator.getRefundStatusesForFiling(filing.filingId());
        
        if (refundInfos.isEmpty()) {
            return refundSummaries;
        }
        
        // 3. For each refund status, create RefundSummary with ETA if needed
        
        // Get ETA predictions for all jurisdictions for this filing
        List<RefundEtaPrediction> etaPredictions = List.of();
        try {
            // Use filing ID from the filing we found (converting string to int)
            int filingIdInt = Integer.parseInt(filing.filingId());
            etaPredictions = refundEtaPredictor.predictEta(filingIdInt);
        } catch (NumberFormatException e) {
            // If filing ID can't be parsed as int, continue without ETA predictions
        }
        
        for (RefundStatusData refundInfo : refundInfos) {
            // Base RefundSummary data
            LocalDate etaDate = null;
            double etaConfidence = 0.0;
            int etaWindowDays = 0;
            
            // Find matching ETA prediction for this jurisdiction
            if (refundInfo.status() != null && !refundInfo.status().isFinal()) {
                for (RefundEtaPrediction eta : etaPredictions) {
                    // For now, use the first available prediction
                    // In a more sophisticated implementation, we would match by jurisdiction
                    if (eta != null) {
                        etaDate = eta.expectedArrivalDate();
                        etaConfidence = eta.confidence();
                        etaWindowDays = eta.windowDays();
                        break;
                    }
                }
            }
            
            RefundSummary summary = new RefundSummary(
                    filing.filingId(),
                    filing.trackingId(),
                    filing.taxYear(),
                    filing.filingDate(),
                    refundInfo.jurisdiction(),
                    filing.refundAmount(),
                    refundInfo.status(),
                    filing.disbursementMethod(),
                    refundInfo.lastUpdatedAt(),
                    etaDate,
                    etaConfidence,
                    etaWindowDays
            );;
            
            refundSummaries.add(summary);
        }
        
        return refundSummaries;
    }
}
