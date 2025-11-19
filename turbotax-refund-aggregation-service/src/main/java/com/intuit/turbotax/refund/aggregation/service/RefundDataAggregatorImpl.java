package com.intuit.turbotax.refund.aggregation.service;

import java.util.Optional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.RestController; 

import com.intuit.turbotax.api.service.RefundDataAggregator;
import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.refund.aggregation.orchestration.RefundStatusAggregate;
import com.intuit.turbotax.refund.aggregation.orchestration.RefundStatusRepository;
import com.intuit.turbotax.refund.aggregation.client.ExternalIrsClient;
import com.intuit.turbotax.refund.aggregation.client.ExternalStateTaxClient;
import com.intuit.turbotax.refund.aggregation.client.MoneyMovementClient;
import com.intuit.turbotax.api.service.Cache;


@RestController
public class RefundDataAggregatorImpl implements RefundDataAggregator {
    private static final Logger LOG = LoggerFactory.getLogger(RefundDataAggregatorImpl.class);

    private final RefundStatusRepository repository;
    private final Cache<List<RefundStatusData>> cache;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateClient;
    private final MoneyMovementClient moneyMovementClient;

    public RefundDataAggregatorImpl(RefundStatusRepository repository,
            Cache<List<RefundStatusData>> cache,
            ExternalIrsClient irsClient,
            ExternalStateTaxClient stateClient,
            MoneyMovementClient moneyMovementClient) {
        this.repository = repository;
        this.cache = cache;
        this.irsClient = irsClient;
        this.stateClient = stateClient;
        this.moneyMovementClient = moneyMovementClient;
    }

    @Override
    @GetMapping(
        value = "/aggregate-status/{filingId}",
        produces = "application/json")
    public List<RefundStatusData> getRefundStatusesForFiling(@PathVariable int filingId) {
        LOG.debug("Getting refund statuses for filingId={}", filingId);
        
        // Check cache first
        Optional<List<RefundStatusData>> cached = cache.get(String.valueOf(filingId));
        if (cached.isPresent()) {
            LOG.debug("Cache hit for filingId={}, returning {} statuses", filingId, cached.get().size());
            return cached.get();
        }   
        
        LOG.debug("Cache miss for filingId={}, querying repository", filingId);
        // Get statuses from repository
        List<RefundStatusAggregate> statuses = repository.findByFilingId(filingId);
        if (statuses.isEmpty()) {
            LOG.debug("No refund statuses found for filingId={}", filingId);
            return List.of();
        }

        LOG.debug("Found {} refund statuses for filingId={}", statuses.size(), filingId);
        // Convert to aggregator DTOs
        List<RefundStatusData> result = convertToAggregatorDtos(filingId, statuses);
        
        // Cache the result
        cache.put(String.valueOf(filingId), result);
        LOG.debug("Cached {} refund statuses for filingId={}", result.size(), filingId);
        
        return result;
    }

    /**
     * Converts a list of RefundStatus domain objects to a list of RefundStatusAggregatorDto,
     * creating one DTO for each status in the input list.
     */
    private List<RefundStatusData> convertToAggregatorDtos(int filingId, List<RefundStatusAggregate> statuses) {
        if (statuses.isEmpty()) {
            return List.of();
        }

        return statuses.stream()
                .map(status -> new RefundStatusData(
                        filingId,
                        status.status(),
                        status.jurisdiction(),
                        status.lastUpdatedAt()
                ))
                .toList();
    }
}
