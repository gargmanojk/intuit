package com.intuit.turbotax.filing.query.exception;

/**
 * Exception thrown when an invalid user ID is provided.
 */
public class InvalidUserException extends RuntimeException {

    public InvalidUserException(String userId) {
        super("Invalid user ID: " + userId);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}