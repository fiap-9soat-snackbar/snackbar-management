package com.snackbar.iam.infrastructure.controllers.dto;

import com.snackbar.iam.domain.IamRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseDTOTest {

    @Test
    @DisplayName("Should create user response with all fields")
    void shouldCreateUserResponseWithAllFields() {
        // When
        UserResponseDTO userResponse = new UserResponseDTO(
                "user-id-123",
                "John Doe",
                "john@example.com",
                "12345678900",
                IamRole.CONSUMER
        );
        
        // Then
        assertEquals("user-id-123", userResponse.id());
        assertEquals("John Doe", userResponse.name());
        assertEquals("john@example.com", userResponse.email());
        assertEquals("12345678900", userResponse.cpf());
        assertEquals(IamRole.CONSUMER, userResponse.role());
    }
    
    @Test
    @DisplayName("Should create user response with admin role")
    void shouldCreateUserResponseWithAdminRole() {
        // When
        UserResponseDTO userResponse = new UserResponseDTO(
                "admin-id-456",
                "Admin User",
                "admin@example.com",
                "98765432100",
                IamRole.ADMIN
        );
        
        // Then
        assertEquals("admin-id-456", userResponse.id());
        assertEquals("Admin User", userResponse.name());
        assertEquals("admin@example.com", userResponse.email());
        assertEquals("98765432100", userResponse.cpf());
        assertEquals(IamRole.ADMIN, userResponse.role());
    }
}
