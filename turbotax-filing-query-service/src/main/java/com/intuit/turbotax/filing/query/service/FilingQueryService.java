package com.intuit.turbotax.filing.query.service;

import java.util.List;

import com.intuit.turbotax.api.v1.filing.model.TaxFiling;

/**
 * Service interface for tax filing query operations.
 * Provides business logic for retrieving and managing tax filing information.
 */
public interface FilingQueryService {

    /**
     * Retrieves all tax filings for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return list of tax filings for the user
     */
    List<TaxFiling> getFilings(String userId);
}