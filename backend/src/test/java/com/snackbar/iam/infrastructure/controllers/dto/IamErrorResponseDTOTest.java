package com.snackbar.iam.infrastructure.controllers.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IamErrorResponseDTOTest {

    @Test
    @DisplayName("Should create error response with message only")
    void shouldCreateErrorResponseWithMessageOnly() {
        // When
        IamErrorResponseDTO errorResponse = IamErrorResponseDTO.error("Error message");
        
        // Then
        assertFalse(errorResponse.success());
        assertEquals("Error message", errorResponse.message());
        assertNull(errorResponse.data());
    }
    
    @Test
    @DisplayName("Should create error response with message and data")
    void shouldCreateErrorResponseWithMessageAndData() {
        // Given
        Map<String, String> errorData = Map.of("field", "Invalid value");
        
        // When
        IamErrorResponseDTO errorResponse = IamErrorResponseDTO.error("Validation error", errorData);
        
        // Then
        assertFalse(errorResponse.success());
        assertEquals("Validation error", errorResponse.message());
        assertEquals(errorData, errorResponse.data());
    }
    
    @Test
    @DisplayName("Should create error response with direct constructor")
    void shouldCreateErrorResponseWithDirectConstructor() {
        // When
        IamErrorResponseDTO errorResponse = new IamErrorResponseDTO(false, "Direct error", null);
        
        // Then
        assertFalse(errorResponse.success());
        assertEquals("Direct error", errorResponse.message());
        assertNull(errorResponse.data());
    }
}
