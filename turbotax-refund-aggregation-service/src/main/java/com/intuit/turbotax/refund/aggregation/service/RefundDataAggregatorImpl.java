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
    private final Cache<Optional<RefundStatusData>> cache;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateClient;
    private final MoneyMovementClient moneyMovementClient;

    public RefundDataAggregatorImpl(RefundStatusRepository repository,
            Cache<Optional<RefundStatusData>> cache,
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
    public Optional<RefundStatusData> getRefundStatusForFiling(@PathVariable int filingId) {
        LOG.debug("Getting refund status for filingId={}", filingId);
        
        // Check cache first
        Optional<Optional<RefundStatusData>> cached = cache.get(String.valueOf(filingId));
        if (cached.isPresent()) {
            LOG.debug("Cache hit for filingId={}", filingId);
            return cached.get();
        }   
        
        LOG.debug("Cache miss for filingId={}, querying repository", filingId);
        // Get status from repository
        Optional<RefundStatusAggregate> status = repository.findByFilingId(filingId);
        if (status.isEmpty()) {
            LOG.debug("No refund status found for filingId={}", filingId);
            Optional<RefundStatusData> emptyResult = Optional.empty();
            cache.put(String.valueOf(filingId), emptyResult);
            return emptyResult;
        }

        LOG.debug("Found refund status for filingId={}", filingId);
        // Convert to aggregator DTO
        RefundStatusData resultData = convertToAggregatorDto(filingId, status.get());
        Optional<RefundStatusData> result = Optional.of(resultData);
        
        // Cache the result
        cache.put(String.valueOf(filingId), result);
        LOG.debug("Cached refund status for filingId={}", filingId);
        
        return result;
    }

    /**
     * Converts a single RefundStatus domain object to a RefundStatusAggregatorDto.
    */  
    private RefundStatusData convertToAggregatorDto(int filingId, RefundStatusAggregate status) {
        if (status == null) {
            return null;
        }       
        return new RefundStatusData(
                filingId,
                status.status(),
                status.jurisdiction(),
                status.lastUpdatedAt()
        );
    }    
}
