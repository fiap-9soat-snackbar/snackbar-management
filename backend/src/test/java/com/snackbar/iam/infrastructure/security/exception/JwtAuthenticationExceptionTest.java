package com.snackbar.iam.infrastructure.security.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationExceptionTest {

    @Test
    @DisplayName("Should create exception with error type and message")
    void shouldCreateExceptionWithErrorTypeAndMessage() {
        // Given
        JwtAuthenticationException.JwtErrorType errorType = JwtAuthenticationException.JwtErrorType.EXPIRED_TOKEN;
        String message = "Token has expired";
        
        // When
        JwtAuthenticationException exception = new JwtAuthenticationException(errorType, message);
        
        // Then
        assertEquals(errorType, exception.getErrorType());
        assertEquals(message, exception.getMessage());
    }
    
    @ParameterizedTest
    @EnumSource(JwtAuthenticationException.JwtErrorType.class)
    @DisplayName("Should support all error types")
    void shouldSupportAllErrorTypes(JwtAuthenticationException.JwtErrorType errorType) {
        // Given
        String message = "Error message for " + errorType;
        
        // When
        JwtAuthenticationException exception = new JwtAuthenticationException(errorType, message);
        
        // Then
        assertEquals(errorType, exception.getErrorType());
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    @DisplayName("Should extend AuthenticationException")
    void shouldExtendAuthenticationException() {
        // Given
        JwtAuthenticationException exception = new JwtAuthenticationException(
                JwtAuthenticationException.JwtErrorType.INVALID_SIGNATURE, 
                "Invalid signature");
        
        // Then
        assertTrue(exception instanceof org.springframework.security.core.AuthenticationException);
    }
}
