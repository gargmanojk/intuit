package com.intuit.turbotax.api.v1.filing.service;

import java.util.List;

import com.intuit.turbotax.api.v1.filing.model.TaxFiling;

/**
 * Service interface for querying tax filings metadata for a user.
 * Provides methods to retrieve all filings for a user.
 */
public interface FilingQueryService {

    /**
     * Retrieves all tax filings metadata for a given user.
     *
     * @param userId the user identifier
     * @return list of tax filings for the user (may be empty)
     */
    List<TaxFiling> getFilings(String userId);
}