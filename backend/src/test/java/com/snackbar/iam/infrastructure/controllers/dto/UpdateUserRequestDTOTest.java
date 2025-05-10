package com.snackbar.iam.infrastructure.controllers.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestDTOTest {

    @Test
    @DisplayName("Should create update user request with all fields")
    void shouldCreateUpdateUserRequestWithAllFields() {
        // When
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO(
                "John Doe",
                "john@example.com",
                "12345678900",
                "CONSUMER",
                "newpassword"
        );
        
        // Then
        assertEquals("John Doe", updateRequest.getName());
        assertEquals("john@example.com", updateRequest.getEmail());
        assertEquals("12345678900", updateRequest.getCpf());
        assertEquals("CONSUMER", updateRequest.getRole());
        assertEquals("newpassword", updateRequest.getPassword());
    }
    
    @Test
    @DisplayName("Should create update user request with default constructor")
    void shouldCreateUpdateUserRequestWithDefaultConstructor() {
        // When
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO();
        
        // Then
        assertNull(updateRequest.getName());
        assertNull(updateRequest.getEmail());
        assertNull(updateRequest.getCpf());
        assertNull(updateRequest.getRole());
        assertNull(updateRequest.getPassword());
    }
    
    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        // Given
        UpdateUserRequestDTO updateRequest = new UpdateUserRequestDTO();
        
        // When
        updateRequest.setName("Jane Doe");
        updateRequest.setEmail("jane@example.com");
        updateRequest.setCpf("98765432100");
        updateRequest.setRole("ADMIN");
        updateRequest.setPassword("securepassword");
        
        // Then
        assertEquals("Jane Doe", updateRequest.getName());
        assertEquals("jane@example.com", updateRequest.getEmail());
        assertEquals("98765432100", updateRequest.getCpf());
        assertEquals("ADMIN", updateRequest.getRole());
        assertEquals("securepassword", updateRequest.getPassword());
    }
}
