package com.intuit.turbotax.refund.aggregation.service;

import java.util.Optional;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.RestController; 

import com.intuit.turbotax.api.service.RefundDataAggregator;
import com.intuit.turbotax.api.model.RefundStatusData;
import com.intuit.turbotax.refund.aggregation.orchestration.RefundStatus;
import com.intuit.turbotax.refund.aggregation.orchestration.RefundStatusRepository;
import com.intuit.turbotax.refund.aggregation.client.ExternalIrsClient;
import com.intuit.turbotax.refund.aggregation.client.ExternalStateTaxClient;
import com.intuit.turbotax.refund.aggregation.client.MoneyMovementClient;
import com.intuit.turbotax.api.service.Cache;


@RestController
public class RefundDataAggregatorImpl implements RefundDataAggregator {

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
    public List<RefundStatusData> getRefundStatusesForFiling(@PathVariable String filingId) {
        // Check cache first
        Optional<List<RefundStatusData>> cached = cache.get(filingId);
        if (cached.isPresent()) {
            return cached.get();
        }   
        
        // Get statuses from repository
        List<RefundStatus> statuses = repository.findByFilingId(filingId);
        if (statuses.isEmpty()) {
            return List.of();
        }

        // Convert to aggregator DTOs
        List<RefundStatusData> result = convertToAggregatorDtos(filingId, statuses);
        
        // Cache the result
        cache.put(filingId, result);
        
        return result;
    }

    /**
     * Converts a list of RefundStatus domain objects to a list of RefundStatusAggregatorDto,
     * creating one DTO for each status in the input list.
     */
    private List<RefundStatusData> convertToAggregatorDtos(String filingId, List<RefundStatus> statuses) {
        if (statuses.isEmpty()) {
            return List.of();
        }

        return statuses.stream()
                .map(status -> {
                    RefundStatusData.RefundStatusDataBuilder builder = RefundStatusData.builder()
                            .filingId(filingId)
                            .jurisdiction(status.getJurisdiction())
                            .status(status.getStatus())
                            .lastUpdatedAt(status.getLastUpdatedAt());

                    return builder.build();
                })
                .toList();
    }
}
