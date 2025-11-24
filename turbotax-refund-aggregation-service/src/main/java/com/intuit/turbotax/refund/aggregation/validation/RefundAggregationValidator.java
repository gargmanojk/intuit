package com.intuit.turbotax.refund.aggregation.validation;

import org.springframework.stereotype.Component;

import com.intuit.turbotax.refund.aggregation.exception.InvalidFilingIdException;

/**
 * Validator for refund aggregation operations.
 * Provides validation logic for input parameters and business rules.
 */
@Component
public class RefundAggregationValidator {

    /**
     * Validates a filing ID.
     *
     * @param filingId the filing ID to validate
     * @throws InvalidFilingIdException if the filing ID is invalid
     */
    public void validateFilingId(int filingId) {
        if (filingId <= 0) {
            throw new InvalidFilingIdException("Filing ID must be positive, got: " + filingId);
        }
    }

    /**
     * Validates that a required object is not null.
     *
     * @param object    the object to check
     * @param fieldName the name of the field for error messages
     * @throws IllegalArgumentException if the object is null
     */
    public void validateNotNull(Object object, String fieldName) {
        if (object == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
}