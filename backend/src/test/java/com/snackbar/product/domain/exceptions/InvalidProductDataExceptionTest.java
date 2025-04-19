package com.snackbar.product.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InvalidProductDataExceptionTest {

    @Test
    @DisplayName("Should create InvalidProductDataException with message")
    void constructor_ShouldCreateExceptionWithMessage() {
        // Given
        String errorMessage = "Invalid product data: price cannot be negative";

        // When
        InvalidProductDataException exception = new InvalidProductDataException(errorMessage);

        // Then
        assertEquals(errorMessage, exception.getMessage());
    }
}
