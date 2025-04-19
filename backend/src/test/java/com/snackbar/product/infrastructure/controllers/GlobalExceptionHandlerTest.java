package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        assertFalse(response.getBody().success());
        assertEquals("Product not found", response.getBody().message());
        assertNull(response.getBody().data());
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
        assertFalse(response.getBody().success());
        assertEquals("Invalid product data", response.getBody().message());
        assertNull(response.getBody().data());
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
        assertFalse(response.getBody().success());
        assertEquals("Illegal argument", response.getBody().message());
        assertNull(response.getBody().data());
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
        assertFalse(response.getBody().success());
        assertEquals("An unexpected error occurred", response.getBody().message());
        assertNull(response.getBody().data());
    }
}
