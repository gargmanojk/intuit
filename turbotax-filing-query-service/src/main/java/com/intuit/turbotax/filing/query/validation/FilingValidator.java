package com.intuit.turbotax.filing.query.validation;

import org.springframework.stereotype.Component;

import com.intuit.turbotax.filing.query.exception.InvalidUserException;

/**
 * Validator for filing-related operations.
 */
@Component
public class FilingValidator {

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
}