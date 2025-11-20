package com.intuit.turbotax.filing.query.repository;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Repository interface for managing tax filing entities.
 * Provides methods to query and retrieve tax filing information.
 */
public interface TaxFilingRepository {
    
    /**
     * Finds all tax filings for a specific user.
     * Returns a stream of filing entities that can be processed lazily.
     * 
     * @param userId the unique identifier of the user
     * @return a stream of tax filing entities for the specified user
     * @throws IllegalArgumentException if userId is null or empty
     */
    Stream<TaxFilingEntity> findLatestByUserId(String userId);
    
    /**
     * Finds a specific tax filing by its filing ID.
     * 
     * @param filingId the unique filing identifier
     * @return an Optional containing the tax filing entity if found, empty otherwise
     */
    Optional<TaxFilingEntity> findByFilingId(int filingId);
}
