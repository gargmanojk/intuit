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
import com.intuit.turbotax.api.service.Cache;

@Service
public class RefundQueryOrchestrator {

    private final FilingQueryService filingQueryService;
    private final RefundDataAggregator refundDataAggregator;
    private final RefundEtaPredictor refundEtaPredictor;
    private final Cache<List<RefundSummary>> refundSummaryCache;

    public RefundQueryOrchestrator(FilingQueryService filingQueryService,
                                    RefundDataAggregator refundDataAggregator,
                                    RefundEtaPredictor refundEtaPredictor,
                                    Cache<List<RefundSummary>> refundSummaryCache) {
        this.filingQueryService = filingQueryService;
        this.refundDataAggregator = refundDataAggregator;
        this.refundEtaPredictor = refundEtaPredictor;
        this.refundSummaryCache = refundSummaryCache;
    }

    public List<RefundSummary> getLatestRefundStatus(String userId) {
        // Check cache first
        Optional<List<RefundSummary>> cached = refundSummaryCache.get(userId);
        if (cached.isPresent()) {
            return cached.get();
        }
        
        List<RefundSummary> refundSummaries = new ArrayList<>();
        
        // 1. Find latest filing for this user
        List<TaxFiling> filings = filingQueryService.findLatestFilingForUser(userId);
        
        if (filings.isEmpty()) {
            // No filing found – user hasn't filed or data not available
            return refundSummaries;
        }
        
        // 2. Process each filing (federal and state filings separately)
        for (TaxFiling filing : filings) {
            // 3. Fetch refund statuses for this specific filing
            List<RefundStatusData> refundInfos = refundDataAggregator.getRefundStatusesForFiling(filing.filingId());
            
            if (refundInfos.isEmpty()) {
                // No refund data found for this filing
                continue;
            }
            
            // 4. Get ETA predictions for this filing
            List<RefundEtaPrediction> etaPredictions = List.of();
            try {
                etaPredictions = refundEtaPredictor.predictEta(filing.filingId());
            } catch (Exception e) {
                // If ETA prediction fails, continue without ETA predictions
            }
            
            // 5. For each refund status, create RefundSummary with ETA if needed
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
                );
                
                refundSummaries.add(summary);
            }
        }
        
        // Cache the result before returning
        refundSummaryCache.put(userId, refundSummaries);
        
        return refundSummaries;
    }
}
