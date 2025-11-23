package com.intuit.turbotax.api.v1.filing.service;

import java.util.List;
import java.util.Optional;

import com.intuit.turbotax.api.v1.filing.model.TaxFiling;

/**
 * Service interface for querying tax filings metadata for a user.
 * Provides methods to retrieve all filings for a user or a specific filing by
 * ID.
 */
public interface FilingQueryService {

    /**
     * Retrieves all tax filings metadata for a given user.
     *
     * @param userId the user identifier
     * @return list of tax filings for the user (may be empty)
     */
    List<TaxFiling> getFilings(String userId);

    /**
     * Retrieves a specific tax filing metadata by its filing ID.
     *
     * @param filingId the filing identifier
     * @return an Optional containing the tax filing if found, or empty if not found
     */
    Optional<TaxFiling> getFiling(int filingId);
}