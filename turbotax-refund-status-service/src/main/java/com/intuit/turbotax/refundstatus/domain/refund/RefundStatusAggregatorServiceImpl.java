package com.intuit.turbotax.refundstatus.domain.refund;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefundStatusAggregatorServiceImpl implements RefundStatusAggregatorService {

    private final RefundStatusRepository repository;
    private final RefundStatusCache cache;
    private final ExternalIrsClient irsClient;
    private final ExternalStateTaxClient stateClient;
    private final MoneyMovementClient moneyMovementClient;

    public RefundStatusAggregatorServiceImpl(RefundStatusRepository repository,
            RefundStatusCache cache,
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
    public List<RefundStatus> getRefundStatusesForFiling(String filingId) {
        // Mock implementation: just read from cache/DB.
        // Real implementation would refresh async via polling/webhooks.
        List<RefundStatus> cached = cache.getStatuses(filingId);
        if (!cached.isEmpty()) {
            return cached;
        }

        List<RefundStatus> fromDb = repository.findByFilingId(filingId);
        cache.putStatuses(filingId, fromDb);
        return fromDb;
    }
}
