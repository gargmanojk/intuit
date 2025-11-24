package com.intuit.turbotax.refund.aggregation.validation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.intuit.turbotax.refund.aggregation.exception.InvalidFilingIdException;

class RefundAggregationValidatorTest {

    private final RefundAggregationValidator validator = new RefundAggregationValidator();

    @Test
    void validateFilingId_ShouldThrowException_WhenFilingIdIsNegative() {
        // When & Then
        assertThatThrownBy(() -> validator.validateFilingId(-1))
                .isInstanceOf(InvalidFilingIdException.class)
                .hasMessage("Filing ID must be positive, got: -1");
    }

    @Test
    void validateFilingId_ShouldThrowException_WhenFilingIdIsZero() {
        // When & Then
        assertThatThrownBy(() -> validator.validateFilingId(0))
                .isInstanceOf(InvalidFilingIdException.class)
                .hasMessage("Filing ID must be positive, got: 0");
    }

    @Test
    void validateFilingId_ShouldPass_WhenFilingIdIsPositive() {
        // When & Then - should not throw
        validator.validateFilingId(12345);
    }

    @Test
    void validateNotNull_ShouldThrowException_WhenObjectIsNull() {
        // When & Then
        assertThatThrownBy(() -> validator.validateNotNull(null, "testObject"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("testObject cannot be null");
    }

    @Test
    void validateNotNull_ShouldPass_WhenObjectIsNotNull() {
        // When & Then - should not throw
        validator.validateNotNull("test", "testObject");
    }
}