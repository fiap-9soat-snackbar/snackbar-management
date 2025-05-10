package com.snackbar.iam.infrastructure.config;

import com.snackbar.iam.infrastructure.security.IamJwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IamSecurityConfigTest {

    @Mock
    private IamJwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Should create IamSecurityConfig with required dependencies")
    void shouldCreateIamSecurityConfigWithRequiredDependencies() {
        // When
        IamSecurityConfig securityConfig = new IamSecurityConfig(jwtAuthenticationFilter, userDetailsService);
        
        // Then
        assertNotNull(securityConfig);
    }
    
    @Test
    @DisplayName("Should have securityFilterChain method that returns SecurityFilterChain")
    void shouldHaveSecurityFilterChainMethod() {
        // When/Then - Verify the method exists and has the correct signature
        assertDoesNotThrow(() -> {
            java.lang.reflect.Method method = IamSecurityConfig.class.getMethod("securityFilterChain", HttpSecurity.class);
            assertEquals(SecurityFilterChain.class, method.getReturnType());
        });
    }
    
    @Test
    @DisplayName("Should have proper annotations on securityFilterChain method")
    void shouldHaveProperAnnotationsOnSecurityFilterChainMethod() throws Exception {
        // When/Then - Verify the method has the required annotations
        java.lang.reflect.Method method = IamSecurityConfig.class.getMethod("securityFilterChain", HttpSecurity.class);
        
        // Check for @Bean annotation
        assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
        
        // Check for @Order annotation
        assertTrue(method.isAnnotationPresent(org.springframework.core.annotation.Order.class));
        org.springframework.core.annotation.Order orderAnnotation = 
                method.getAnnotation(org.springframework.core.annotation.Order.class);
        assertEquals(1, orderAnnotation.value());
    }
    
    @Test
    @DisplayName("Should have proper class annotations")
    void shouldHaveProperClassAnnotations() {
        // When/Then - Verify the class has the required annotations
        Class<?> clazz = IamSecurityConfig.class;
        
        // Check for @Configuration annotation
        assertTrue(clazz.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        
        // Check for @EnableWebSecurity annotation
        assertTrue(clazz.isAnnotationPresent(org.springframework.security.config.annotation.web.configuration.EnableWebSecurity.class));
        
        // Check for @EnableMethodSecurity annotation
        assertTrue(clazz.isAnnotationPresent(org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity.class));
    }
}
