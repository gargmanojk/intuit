package com.intuit.turbotax.filing.query.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;

@Repository
public class TaxFilingRepositoryImpl implements TaxFilingRepository {

        // In-memory storage using concurrent map for thread safety
        private final Map<Integer, TaxFilingEntity> filingStore = new ConcurrentHashMap<>();

        public TaxFilingRepositoryImpl() {
                // Data initialization is now handled by DataInitializationConfig
        }

        @Override
        public Stream<TaxFilingEntity> findLatestByUserId(String userId) {
                // Return all filings for the specified user
                return filingStore.values().stream()
                                .filter(filing -> userId.equals(filing.getUserId()));
        }

        /**
         * Adds a new filing to the in-memory store
         */
        public void save(TaxFilingEntity filing) {
                filingStore.put(filing.getFilingId(), filing);
                System.out.println(
                                "Saved filing for filingId: " + filing.getFilingId() + ", user: " + filing.getUserId());
        }
}
