package com.intuit.turbotax.refund.aggregation.exception;

/**
 * Exception thrown when an invalid filing ID is provided.
 */
public class InvalidFilingIdException extends RuntimeException {

    public InvalidFilingIdException(String message) {
        super(message);
    }

    public InvalidFilingIdException(String message, Throwable cause) {
        super(message, cause);
    }
}