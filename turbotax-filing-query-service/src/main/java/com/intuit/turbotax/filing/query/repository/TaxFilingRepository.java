package com.intuit.turbotax.filing.query.repository;

import java.util.List;
import java.util.Optional;

public interface TaxFilingRepository {
    
    List<TaxFilingEntity> findLatestByUserId(String userId);
    Optional<TaxFilingEntity> findByFilingId(int filingId);
}
