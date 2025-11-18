package com.intuit.turbotax.aggregator.api;

import java.util.Optional;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.RestController; 

import com.intuit.turbotax.contract.service.RefundStatusAggregatorService;
import com.intuit.turbotax.contract.data.RefundInfo;
import com.intuit.turbotax.aggregator.domain.RefundStatus;
import com.intuit.turbotax.aggregator.domain.RefundStatusRepository;
import com.intuit.turbotax.aggregator.integration.ExternalIrsClient;
import com.intuit.turbotax.aggregator.integration.ExternalStateTaxClient;
import com.intuit.turbotax.aggregator.integration.MoneyMovementClient;
import com.intuit.turbotax.contract.service.Cache;


@RestController
public class RefundStatusAggregatorServiceImpl implements RefundStatusAggregatorService {

    private final RefundStatusRepository repository;
    private final Cache<List<RefundInfo>> cache;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateClient;
    private final MoneyMovementClient moneyMovementClient;

    public RefundStatusAggregatorServiceImpl(RefundStatusRepository repository,
            Cache<List<RefundInfo>> cache,
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
    public List<RefundInfo> getRefundStatusesForFiling(@PathVariable String filingId) {
        // Check cache first
        Optional<List<RefundInfo>> cached = cache.get(filingId);
        if (cached.isPresent()) {
            return cached.get();
        }   
        
        // Get statuses from repository
        List<RefundStatus> statuses = repository.findByFilingId(filingId);
        if (statuses.isEmpty()) {
            return List.of();
        }

        // Convert to aggregator DTOs
        List<RefundInfo> result = convertToAggregatorDtos(filingId, statuses);
        
        // Cache the result
        cache.put(filingId, result);
        
        return result;
    }

    /**
     * Converts a list of RefundStatus domain objects to a list of RefundStatusAggregatorDto,
     * creating one DTO for each status in the input list.
     */
    private List<RefundInfo> convertToAggregatorDtos(String filingId, List<RefundStatus> statuses) {
        if (statuses.isEmpty()) {
            return List.of();
        }

        return statuses.stream()
                .map(status -> {
                    RefundInfo.RefundInfoBuilder builder = RefundInfo.builder()
                            .filingId(filingId)
                            .jurisdiction(status.getJurisdiction())
                            .status(status.getStatus())
                            .lastUpdatedAt(status.getLastUpdatedAt());

                    return builder.build();
                })
                .toList();
    }
}
