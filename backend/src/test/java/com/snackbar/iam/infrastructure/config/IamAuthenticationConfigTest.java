package com.snackbar.iam.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IamAuthenticationConfigTest {

    private final IamAuthenticationConfig config = new IamAuthenticationConfig();

    @Test
    @DisplayName("Should create BCryptPasswordEncoder")
    void shouldCreateBCryptPasswordEncoder() {
        // When
        PasswordEncoder passwordEncoder = config.passwordEncoder();
        
        // Then
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }
    
    @Test
    @DisplayName("Should return AuthenticationManager from configuration")
    void shouldReturnAuthenticationManagerFromConfiguration() throws Exception {
        // Given
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(expectedManager);
        
        // When
        AuthenticationManager result = config.authenticationManager(authConfig);
        
        // Then
        assertNotNull(result);
        assertEquals(expectedManager, result);
        verify(authConfig).getAuthenticationManager();
    }
}
