package com.intuit.turbotax.filing.data.repository;

import java.util.List;

public interface TaxFilingRepository {
    
    List<TaxFilingEntity> findLatestByUserId(String userId);
}
