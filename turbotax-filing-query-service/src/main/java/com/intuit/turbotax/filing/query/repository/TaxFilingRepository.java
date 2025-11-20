package com.intuit.turbotax.filing.query.repository;

import java.util.Optional;
import java.util.stream.Stream;

public interface TaxFilingRepository {
    
    Stream<TaxFilingEntity> findLatestByUserId(String userId);
    Optional<TaxFilingEntity> findByFilingId(int filingId);
}
