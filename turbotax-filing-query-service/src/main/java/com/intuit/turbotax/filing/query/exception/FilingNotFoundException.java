package com.intuit.turbotax.filing.query.exception;

/**
 * Exception thrown when a requested filing is not found.
 */
public class FilingNotFoundException extends RuntimeException {

    public FilingNotFoundException(int filingId) {
        super("Filing not found with ID: " + filingId);
    }

    public FilingNotFoundException(String message) {
        super(message);
    }
}