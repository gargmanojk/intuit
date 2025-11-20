package com.intuit.turbotax.refund.aggregation.repository;

import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Component;
import com.intuit.turbotax.api.model.Jurisdiction;

@Component
public class RefundStatusRepositoryImpl implements RefundStatusRepository {

    // In-memory storage using concurrent map for thread safety
    private final Map<Integer, RefundStatusAggregate> aggregateStore = new ConcurrentHashMap<>();
    
    public RefundStatusRepositoryImpl() {
        // Initialize with some sample data
        initializeSampleData();
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
        System.out.println("Saved aggregate for filingId: " + aggregate.filingId() + " with status: " + aggregate.status());
    }
    
    private void initializeSampleData() {
        // Add some sample federal filings
        aggregateStore.put(202410001, new RefundStatusAggregate(
            202410001,
            "IRS-TRACK-202410001",
            Jurisdiction.FEDERAL,
            com.intuit.turbotax.api.model.RefundStatus.PROCESSING,
            "FED_202410001",
            "Your federal tax refund is being processed by the IRS",
            Instant.now(),
            BigDecimal.valueOf(2500)
        ));
        
        aggregateStore.put(202410002, new RefundStatusAggregate(
            202410002,
            "CA-TRACK-202410002",
            Jurisdiction.STATE_CA,
            com.intuit.turbotax.api.model.RefundStatus.ACCEPTED,
            "CA_202410002",
            "Your California state tax refund has been accepted",
            Instant.now(),
            BigDecimal.valueOf(350)
        ));
        
        aggregateStore.put(202410003, new RefundStatusAggregate(
            202410003,
            "IRS-TRACK-202410003",
            Jurisdiction.FEDERAL,
            com.intuit.turbotax.api.model.RefundStatus.FILED,
            "FED_202410003",
            "Your federal tax return has been filed",
            Instant.now(),
            BigDecimal.valueOf(1800)
        ));
        
        aggregateStore.put(202410004, new RefundStatusAggregate(
            202410004,
            "NY-TRACK-202410004",
            Jurisdiction.STATE_NY,
            com.intuit.turbotax.api.model.RefundStatus.PROCESSING,
            "NY_202410004",
            "Your New York state tax refund is being processed",
            Instant.now(),
            BigDecimal.valueOf(450)
        ));
    }
}