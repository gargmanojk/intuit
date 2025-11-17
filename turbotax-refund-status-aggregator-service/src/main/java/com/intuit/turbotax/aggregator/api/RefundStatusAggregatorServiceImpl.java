package com.intuit.turbotax.aggregator.api;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.RestController; 

import com.intuit.turbotax.aggregator.api.RefundStatusAggregatorService;
import com.intuit.turbotax.domainmodel.dto.RefundStatusAggregatorResponse;
import com.intuit.turbotax.aggregator.domain.RefundStatus;
import com.intuit.turbotax.aggregator.domain.RefundStatusRepository;
import com.intuit.turbotax.aggregator.integration.ExternalIrsClient;
import com.intuit.turbotax.aggregator.integration.ExternalStateTaxClient;
import com.intuit.turbotax.aggregator.integration.MoneyMovementClient;
import com.intuit.turbotax.aggregator.integration.Cache;


@RestController
public class RefundStatusAggregatorServiceImpl implements RefundStatusAggregatorService {

    private final RefundStatusRepository repository;
    private final Cache<RefundStatusAggregatorResponse> cache;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateClient;
    private final MoneyMovementClient moneyMovementClient;

    public RefundStatusAggregatorServiceImpl(RefundStatusRepository repository,
            Cache<RefundStatusAggregatorResponse> cache,
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
    public Optional<RefundStatusAggregatorResponse> getRefundStatusesForFiling(@PathVariable String filingId) {
        // Mock implementation: read from cache/DB and convert to DTO response.
        // Real implementation would refresh async via polling/webhooks.
        Optional<RefundStatusAggregatorResponse> cached = cache.get(filingId);
        if (cached.isPresent()) {
            return cached;
        }   
        
        List<RefundStatus> statuses = repository.findByFilingId(filingId);
        if (statuses.isEmpty()) {
            return Optional.empty();
        }

        // Find federal and first state status
        RefundStatus federal = statuses.stream()
                .filter(s -> s.getJurisdiction() == com.intuit.turbotax.domainmodel.Jurisdiction.FEDERAL)
                .findFirst().orElse(null);

        RefundStatus state = statuses.stream()
                .filter(s -> s.getJurisdiction() != com.intuit.turbotax.domainmodel.Jurisdiction.FEDERAL)
                .findFirst().orElse(null);

        RefundStatusAggregatorResponse.RefundStatusAggregatorResponseBuilder builder = RefundStatusAggregatorResponse.builder()
                .filingId(filingId);

        if (federal != null) {
            builder.federalStatus(federal.getStatusId())
                    .federalCanonicalStatus(federal.getCanonicalStatus())
                    .federalRawStatusCode(federal.getRawStatusCode())
                    .federalStatusMessageKey(federal.getStatusMessageKey())
                    .federalStatusLastUpdatedAt(federal.getStatusLastUpdatedAt())
                    .federalAmount(federal.getAmount());
        }

        if (state != null) {
            builder.stateStatus(state.getStatusId())
                    .stateJurisdiction(state.getJurisdiction())
                    .stateCanonicalStatus(state.getCanonicalStatus())
                    .stateRawStatusCode(state.getRawStatusCode())
                    .stateStatusMessageKey(state.getStatusMessageKey())
                    .stateStatusLastUpdatedAt(state.getStatusLastUpdatedAt())
                    .stateAmount(state.getAmount());
        }
        
        var response = builder.build();
        cache.put(filingId, response);
        return Optional.of(response);
    }
}
