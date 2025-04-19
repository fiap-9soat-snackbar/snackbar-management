package com.snackbar.product.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductNotFoundExceptionTest {

    @Test
    @DisplayName("Should create ProductNotFoundException with message")
    void constructor_ShouldCreateExceptionWithMessage() {
        // Given
        String errorMessage = "Product not found";

        // When
        ProductNotFoundException exception = new ProductNotFoundException(errorMessage);

        // Then
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should create ProductNotFoundException with id")
    void withId_ShouldCreateExceptionWithIdMessage() {
        // Given
        String id = "123";
        String expectedMessage = "Product not found with id: 123";

        // When
        ProductNotFoundException exception = ProductNotFoundException.withId(id);

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should create ProductNotFoundException with name")
    void withName_ShouldCreateExceptionWithNameMessage() {
        // Given
        String name = "Burger";
        String expectedMessage = "Product not found with name: Burger";

        // When
        ProductNotFoundException exception = ProductNotFoundException.withName(name);

        // Then
        assertEquals(expectedMessage, exception.getMessage());
    }
}
