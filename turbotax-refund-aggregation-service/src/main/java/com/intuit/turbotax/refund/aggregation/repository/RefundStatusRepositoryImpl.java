package com.intuit.turbotax.refund.aggregation.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

@Repository
public class RefundStatusRepositoryImpl implements RefundStatusRepository {

    // In-memory storage using concurrent map for thread safety
    private final Map<Integer, RefundStatusAggregate> aggregateStore = new ConcurrentHashMap<>();

    public RefundStatusRepositoryImpl() {
        // Repository initialization is now handled by RefundStatusRepositoryConfig
    }

    @Override
    public Optional<RefundStatusAggregate> findByFilingId(int filingId) {
        RefundStatusAggregate aggregate = aggregateStore.get(filingId);
        return Optional.ofNullable(aggregate);
    }

    @Override
    public Stream<Integer> getActiveFilingIds() {
        // Return filing IDs that are not in final status
        return aggregateStore.entrySet().stream()
                .filter(entry -> !entry.getValue().status().isFinal())
                .map(Map.Entry::getKey);
    }

    @Override
    public void save(RefundStatusAggregate aggregate) {
        // Store in memory
        aggregateStore.put(aggregate.filingId(), aggregate);
        System.out.println(
                "Saved aggregate for filingId: " + aggregate.filingId() + " with status: " + aggregate.status());
    }
}