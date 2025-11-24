package com.intuit.turbotax.refund.query.validation;

import org.springframework.stereotype.Component;

import com.intuit.turbotax.refund.query.exception.InvalidUserIdException;

/**
 * Validator for refund query operations.
 * Provides validation logic for input parameters and business rules.
 */
@Component
public class RefundQueryValidator {

    /**
     * Validates a user ID.
     *
     * @param userId the user ID to validate
     * @throws InvalidUserIdException if the user ID is invalid
     */
    public void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidUserIdException("User ID cannot be null or empty");
        }
        if (userId.length() > 100) {
            throw new InvalidUserIdException("User ID is too long, maximum length is 100 characters");
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