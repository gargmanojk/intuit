package com.intuit.turbotax.refund.query.exception;

/**
 * Exception thrown when an invalid user ID is provided.
 */
public class InvalidUserIdException extends RuntimeException {

    public InvalidUserIdException(String message) {
        super(message);
    }

    public InvalidUserIdException(String message, Throwable cause) {
        super(message, cause);
    }
}