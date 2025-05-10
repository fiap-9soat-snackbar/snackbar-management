package com.snackbar.iam.infrastructure.controllers.dto;

import com.snackbar.iam.domain.IamRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterUserRequestDTOTest {

    @Test
    @DisplayName("Should create register user request with consumer role")
    void shouldCreateRegisterUserRequestWithConsumerRole() {
        // When
        RegisterUserRequestDTO registerRequest = new RegisterUserRequestDTO(
                "John Doe",
                "john@example.com",
                "12345678900",
                "password123",
                IamRole.CONSUMER
        );
        
        // Then
        assertEquals("John Doe", registerRequest.fullName());
        assertEquals("john@example.com", registerRequest.email());
        assertEquals("12345678900", registerRequest.cpf());
        assertEquals("password123", registerRequest.password());
        assertEquals(IamRole.CONSUMER, registerRequest.role());
    }
    
    @Test
    @DisplayName("Should create register user request with admin role")
    void shouldCreateRegisterUserRequestWithAdminRole() {
        // When
        RegisterUserRequestDTO registerRequest = new RegisterUserRequestDTO(
                "Admin User",
                "admin@example.com",
                "98765432100",
                "adminPass123",
                IamRole.ADMIN
        );
        
        // Then
        assertEquals("Admin User", registerRequest.fullName());
        assertEquals("admin@example.com", registerRequest.email());
        assertEquals("98765432100", registerRequest.cpf());
        assertEquals("adminPass123", registerRequest.password());
        assertEquals(IamRole.ADMIN, registerRequest.role());
    }
}
