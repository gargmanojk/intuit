package com.intuit.turbotax.refundstatus.domain.filing;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryFilingMetadataRepository implements FilingMetadataRepository {

    @Override
    public Optional<FilingMetadata> findLatestByUserId(String userId) {
        // mock data
        FilingMetadata fm = new FilingMetadata();
        // set fields...
        return Optional.of(fm);
    }
}
