package com.snackbar.iam.infrastructure.controllers.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOTest {

    @Test
    @DisplayName("Should create login request with all fields")
    void shouldCreateLoginRequestWithAllFields() {
        // When
        LoginRequestDTO loginRequest = new LoginRequestDTO("12345678900", "password", true);
        
        // Then
        assertEquals("12345678900", loginRequest.cpf());
        assertEquals("password", loginRequest.password());
        assertTrue(loginRequest.anonymous());
    }
    
    @Test
    @DisplayName("Should create login request with default anonymous value")
    void shouldCreateLoginRequestWithDefaultAnonymousValue() {
        // When
        LoginRequestDTO loginRequest = new LoginRequestDTO("12345678900", "password");
        
        // Then
        assertEquals("12345678900", loginRequest.cpf());
        assertEquals("password", loginRequest.password());
        assertFalse(loginRequest.anonymous());
    }
}
