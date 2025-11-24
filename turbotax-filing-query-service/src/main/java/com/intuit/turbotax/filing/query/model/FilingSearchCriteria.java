package com.intuit.turbotax.filing.query.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Criteria for searching tax filings.
 */
public record FilingSearchCriteria(
        @NotBlank(message = "User ID cannot be blank") 
        @Size(min = 3, max = 50, message = "User ID must be between 3 and 50 characters") 
        String userId,

        @Min(value = 2020, message = "Tax year must be 2020 or later") 
        Integer taxYear) {
}