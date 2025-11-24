package com.intuit.turbotax.filing.query.validation;

import org.springframework.stereotype.Component;

import com.intuit.turbotax.filing.query.exception.InvalidUserException;
import com.intuit.turbotax.filing.query.model.FilingSearchCriteria;

/**
 * Validator for filing-related operations.
 */
@Component
public class FilingValidator {

    /**
     * Validates the filing search criteria.
     *
     * @param criteria the search criteria to validate
     * @throws InvalidUserException if validation fails
     */
    public void validateSearchCriteria(FilingSearchCriteria criteria) {
        if (criteria == null) {
            throw new InvalidUserException("Search criteria cannot be null");
        }

        if (criteria.userId() == null || criteria.userId().trim().isEmpty()) {
            throw new InvalidUserException("User ID cannot be null or empty");
        }

        if (criteria.userId().length() < 3 || criteria.userId().length() > 50) {
            throw new InvalidUserException("User ID must be between 3 and 50 characters");
        }

        if (criteria.taxYear() != null && criteria.taxYear() < 2020) {
            throw new InvalidUserException("Tax year must be 2020 or later");
        }
    }

    /**
     * Validates a user ID.
     *
     * @param userId the user ID to validate
     * @throws InvalidUserException if validation fails
     */
    public void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUserException("User ID cannot be null or empty");
        }

        if (userId.length() < 3 || userId.length() > 50) {
            throw new InvalidUserException("User ID must be between 3 and 50 characters");
        }
    }

    /**
     * Validates a filing ID.
     *
     * @param filingId the filing ID to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateFilingId(int filingId) {
        if (filingId <= 0) {
            throw new IllegalArgumentException("Filing ID must be positive");
        }
    }
}