package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.snackbar.product.domain.exceptions.InvalidProductDataException;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import com.snackbar.product.infrastructure.controllers.dto.ResponseDTO;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ProductNotFoundException")
    void handleProductNotFoundException_ShouldReturnNotFoundStatus() {
        // Given
        ProductNotFoundException exception = new ProductNotFoundException("Product not found");

        // When
        ResponseEntity<ResponseDTO> response = exceptionHandler.handleProductNotFoundException(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertFalse(responseBody.success(), "Response should indicate failure");
        assertEquals("Product not found", responseBody.message(), "Response message should match exception message");
        assertNull(responseBody.data(), "Response data should be null");
    }

    @Test
    @DisplayName("Should handle InvalidProductDataException")
    void handleInvalidProductDataException_ShouldReturnBadRequestStatus() {
        // Given
        InvalidProductDataException exception = new InvalidProductDataException("Invalid product data");

        // When
        ResponseEntity<ResponseDTO> response = exceptionHandler.handleInvalidProductDataException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertFalse(responseBody.success(), "Response should indicate failure");
        assertEquals("Invalid product data", responseBody.message(), "Response message should match exception message");
        assertNull(responseBody.data(), "Response data should be null");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void handleIllegalArgumentException_ShouldReturnBadRequestStatus() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");

        // When
        ResponseEntity<ResponseDTO> response = exceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertFalse(responseBody.success(), "Response should indicate failure");
        assertEquals("Illegal argument", responseBody.message(), "Response message should match exception message");
        assertNull(responseBody.data(), "Response data should be null");
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void handleGenericException_ShouldReturnInternalServerErrorStatus() {
        // Given
        Exception exception = new Exception("Some unexpected error");

        // When
        ResponseEntity<ResponseDTO> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertFalse(responseBody.success(), "Response should indicate failure");
        assertEquals("An unexpected error occurred", responseBody.message(), "Response message should be generic error message");
        assertNull(responseBody.data(), "Response data should be null");
    }
}
