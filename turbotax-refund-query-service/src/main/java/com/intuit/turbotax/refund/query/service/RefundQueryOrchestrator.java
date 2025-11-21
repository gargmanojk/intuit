// ...existing code...

package com.intuit.turbotax.refund.query.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.intuit.turbotax.api.model.RefundEtaPrediction;
import com.intuit.turbotax.api.model.TaxFiling;
import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.api.model.RefundSummary;
import com.intuit.turbotax.api.service.RefundPredictor;
import com.intuit.turbotax.refund.query.client.RefundPredictorProxy;
import com.intuit.turbotax.api.service.FilingQueryService;
import com.intuit.turbotax.api.service.RefundDataAggregator;
import com.intuit.turbotax.api.service.Cache;

@Service
public class RefundQueryOrchestrator {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefundQueryOrchestrator.class);

    private final FilingQueryService filingQueryService;
    private final RefundDataAggregator refundDataAggregator;
    private RefundPredictor refundPredictor;
    private final Cache<List<RefundSummary>> refundSummaryCache;

    public RefundQueryOrchestrator(FilingQueryService filingQueryService,
            RefundDataAggregator refundDataAggregator,
            RefundPredictor refundPredictor,
            Cache<List<RefundSummary>> refundSummaryCache) {
        this.filingQueryService = filingQueryService;
        this.refundDataAggregator = refundDataAggregator;
        this.refundPredictor = refundPredictor;
        this.refundSummaryCache = refundSummaryCache;
    }

    public List<RefundSummary> getLatestRefundStatus(String userId) {
        // Check cache first
        Optional<List<RefundSummary>> cached = refundSummaryCache.get(userId);
        if (cached.isPresent()) {
            return cached.get();
        }

        List<RefundSummary> refundSummaries = new ArrayList<>();

        // 1. Find latest filings for this user
        List<TaxFiling> filings = filingQueryService.getFilings(userId);

        if (filings.isEmpty()) {
            // No filing found – user hasn't filed or data not available
            return refundSummaries;
        }

        // 2. Process each filing (federal and state have different filing IDs)
        for (TaxFiling filing : filings) {
            // 3. Fetch refund status for this specific filing ID
            Optional<RefundStatusData> refundInfoOpt = refundDataAggregator.getRefundStatusForFiling(filing.filingId());

            if (refundInfoOpt.isEmpty()) {
                // No refund data found for this filing, continue to next
                continue;
            }

            RefundStatusData refundInfo = refundInfoOpt.get();

            // 4. Build feature map for prediction using RefundFeatureMapper
            Map<com.intuit.turbotax.api.model.PredictionFeature, Object> features = new RefundFeatureMapper()
                    .mapToFeatures(refundInfo, filing);
            printFeatureMap(features);

            // 5. Get ETA prediction for this filing
            Optional<RefundEtaPrediction> etaPrediction = Optional.empty();
            try {
                // If refundPredictor.predictEta returns Optional<Integer>, convert it to
                // Optional<RefundEtaPrediction>
                Optional<Integer> etaDaysOpt = refundPredictor.predictEta(features);
                if (etaDaysOpt.isPresent()) {
                    // Construct RefundEtaPrediction from etaDaysOpt and other required info
                    RefundEtaPrediction eta = new RefundEtaPrediction(
                            LocalDate.now().plusDays(etaDaysOpt.get()), // Example: expectedArrivalDate
                            0.8, // Example: confidence, replace with actual value if available
                            3 // Example: windowDays
                    );
                    etaPrediction = Optional.of(eta);
                }
            } catch (Exception e) {
                LOG.error("Error predicting ETA for filingId={}: {}", filing.filingId(), e.getMessage());
                // If ETA prediction fails, continue without ETA prediction
            }

            // 5. Create refund summary for this filing
            // Base RefundSummary data
            LocalDate etaDate = null;
            double etaConfidence = 0.0;
            int etaWindowDays = 0;

            // Use ETA prediction if available and refund status is not final
            if (refundInfo.status() != null && !refundInfo.status().isFinal() && etaPrediction.isPresent()) {
                RefundEtaPrediction eta = etaPrediction.get();
                etaDate = eta.expectedArrivalDate();
                etaConfidence = eta.confidence();
                etaWindowDays = eta.windowDays();
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
                    etaWindowDays);

            refundSummaries.add(summary);
        }

        // Cache the result before returning
        refundSummaryCache.put(userId, refundSummaries);

        return refundSummaries;
    }

    private void printFeatureMap(Map<?, ?> features) {
        System.out.println("--- Feature Map ---");
        LOG.debug("--- Feature Map ---");   
        for (Map.Entry<?, ?> entry : features.entrySet()) {
            LOG.debug("{} = {}", entry.getKey(), entry.getValue());
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        LOG.debug("-------------------");
        System.out.println("-------------------");
    }
}