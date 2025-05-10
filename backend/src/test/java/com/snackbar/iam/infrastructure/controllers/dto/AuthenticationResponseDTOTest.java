package com.snackbar.iam.infrastructure.controllers.dto;

import com.snackbar.iam.domain.IamRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationResponseDTOTest {

    @Test
    @DisplayName("Should create authentication response with all fields")
    void shouldCreateAuthenticationResponseWithAllFields() {
        // Given
        UserResponseDTO userResponse = new UserResponseDTO(
                "user-id-123",
                "John Doe",
                "john@example.com",
                "12345678900",
                IamRole.CONSUMER
        );
        
        // When
        AuthenticationResponseDTO authResponse = new AuthenticationResponseDTO(
                "jwt-token-xyz",
                3600,
                userResponse
        );
        
        // Then
        assertEquals("jwt-token-xyz", authResponse.token());
        assertEquals(3600, authResponse.expiresIn());
        assertNotNull(authResponse.user());
        assertEquals("user-id-123", authResponse.user().id());
        assertEquals("John Doe", authResponse.user().name());
    }
    
    @Test
    @DisplayName("Should create authentication response without user details")
    void shouldCreateAuthenticationResponseWithoutUserDetails() {
        // When
        AuthenticationResponseDTO authResponse = new AuthenticationResponseDTO("jwt-token-xyz", 3600);
        
        // Then
        assertEquals("jwt-token-xyz", authResponse.token());
        assertEquals(3600, authResponse.expiresIn());
        assertNull(authResponse.user());
    }
}
