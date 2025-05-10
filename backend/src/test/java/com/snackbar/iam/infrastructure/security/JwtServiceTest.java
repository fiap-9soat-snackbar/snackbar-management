package com.snackbar.iam.infrastructure.security;

import com.snackbar.iam.domain.IamRole;
import com.snackbar.iam.domain.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;
    
    private final String SECRET_KEY = "testsecretkeytestsecretkeytestsecretkeytestsecretkeytestsecretkey";
    private final long EXPIRATION_TIME = 3600000; // 1 hour
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION_TIME);
    }
    
    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {
        
        @Test
        @DisplayName("Should generate token for User domain entity")
        void shouldGenerateTokenForUserDomainEntity() {
            // Given
            User user = new User(
                "1",
                "Test User",
                "test@example.com",
                "52998224725", // Valid CPF without formatting
                IamRole.CONSUMER,
                "encoded_password"
            );
            
            // When
            String token = jwtService.generateToken(user);
            
            // Then
            assertNotNull(token);
            assertTrue(token.length() > 0);
            
            // Verify token contents
            String cpf = jwtService.extractUsername(token);
            assertEquals(user.getCpf(), cpf);
            
            String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
            assertEquals(user.getRole().name(), role);
            
            String email = jwtService.extractClaim(token, claims -> claims.get("email", String.class));
            assertEquals(user.getEmail(), email);
            
            String name = jwtService.extractClaim(token, claims -> claims.get("name", String.class));
            assertEquals(user.getName(), name);
        }
        
        @Test
        @DisplayName("Should generate token for UserDetails")
        void shouldGenerateTokenForUserDetails() {
            // Given
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("52998224725");
            
            // When
            String token = jwtService.generateToken(userDetails);
            
            // Then
            assertNotNull(token);
            assertTrue(token.length() > 0);
            
            // Verify token contents
            String username = jwtService.extractUsername(token);
            assertEquals(userDetails.getUsername(), username);
        }
        
        @Test
        @DisplayName("Should generate token with extra claims")
        void shouldGenerateTokenWithExtraClaims() {
            // Given
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("52998224725");
            
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("role", "ADMIN");
            extraClaims.put("email", "admin@example.com");
            
            // When
            String token = jwtService.generateToken(extraClaims, userDetails);
            
            // Then
            assertNotNull(token);
            assertTrue(token.length() > 0);
            
            // Verify token contents
            String username = jwtService.extractUsername(token);
            assertEquals(userDetails.getUsername(), username);
            
            String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
            assertEquals("ADMIN", role);
            
            String email = jwtService.extractClaim(token, claims -> claims.get("email", String.class));
            assertEquals("admin@example.com", email);
        }
    }
    
    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {
        
        @Test
        @DisplayName("Should validate token for User domain entity")
        void shouldValidateTokenForUserDomainEntity() {
            // Given
            User user = new User(
                "1",
                "Test User",
                "test@example.com",
                "52998224725", // Valid CPF without formatting
                IamRole.CONSUMER,
                "encoded_password"
            );
            
            String token = jwtService.generateToken(user);
            
            // When
            boolean isValid = jwtService.isTokenValid(token, user);
            
            // Then
            assertTrue(isValid);
        }
        
        @Test
        @DisplayName("Should validate token for UserDetails")
        void shouldValidateTokenForUserDetails() {
            // Given
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("52998224725");
            
            String token = jwtService.generateToken(userDetails);
            
            // When
            boolean isValid = jwtService.isTokenValid(token, userDetails);
            
            // Then
            assertTrue(isValid);
        }
        
        @Test
        @DisplayName("Should return false for token with different username")
        void shouldReturnFalseForTokenWithDifferentUsername() {
            // Given
            User user1 = new User(
                "1",
                "Test User",
                "test@example.com",
                "52998224725", // Valid CPF without formatting
                IamRole.CONSUMER,
                "encoded_password"
            );
            
            User user2 = new User(
                "2",
                "Another User",
                "another@example.com",
                "98765432100", // Different CPF
                IamRole.CONSUMER,
                "encoded_password"
            );
            
            String token = jwtService.generateToken(user1);
            
            // When
            boolean isValid = jwtService.isTokenValid(token, user2);
            
            // Then
            assertFalse(isValid);
        }
    }
    
    @Nested
    @DisplayName("Token Extraction Tests")
    class TokenExtractionTests {
        
        @Test
        @DisplayName("Should extract username from token")
        void shouldExtractUsernameFromToken() {
            // Given
            String cpf = "52998224725"; // Valid CPF without formatting
            User user = new User(
                "1",
                "Test User",
                "test@example.com",
                cpf,
                IamRole.CONSUMER,
                "encoded_password"
            );
            
            String token = jwtService.generateToken(user);
            
            // When
            String extractedCpf = jwtService.extractUsername(token);
            
            // Then
            assertEquals(cpf, extractedCpf);
        }
        
        @Test
        @DisplayName("Should extract claim from token")
        void shouldExtractClaimFromToken() {
            // Given
            User user = new User(
                "1",
                "Test User",
                "test@example.com",
                "52998224725", // Valid CPF without formatting
                IamRole.CONSUMER,
                "encoded_password"
            );
            
            String token = jwtService.generateToken(user);
            
            // When
            String email = jwtService.extractClaim(token, claims -> claims.get("email", String.class));
            
            // Then
            assertEquals(user.getEmail(), email);
        }
    }
    
    @Test
    @DisplayName("Should return configured expiration time")
    void shouldReturnConfiguredExpirationTime() {
        // When
        long expirationTime = jwtService.getExpirationTime();
        
        // Then
        assertEquals(EXPIRATION_TIME, expirationTime);
    }
}
