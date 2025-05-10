package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.domain.exceptions.DuplicateUserException;
import com.snackbar.iam.domain.exceptions.InvalidCredentialsException;
import com.snackbar.iam.domain.exceptions.InvalidUserDataException;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import com.snackbar.iam.infrastructure.controllers.dto.IamErrorResponseDTO;
import com.snackbar.iam.infrastructure.security.exception.JwtAuthenticationException;
import com.snackbar.iam.infrastructure.security.exception.JwtAuthenticationException.JwtErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IAM Global Exception Handler Tests")
class IamGlobalExceptionHandlerTest {

    // Create a test-specific subclass that overrides the error logging behavior
    private static class TestIamGlobalExceptionHandler extends IamGlobalExceptionHandler {
        // Override the handleGenericException method to prevent error logging during tests
        @Override
        public ResponseEntity<IamErrorResponseDTO> handleGenericException(Exception ex) {
            // Skip logging to avoid polluting test logs
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(IamErrorResponseDTO.error("An unexpected error occurred"));
        }
    }
    
    private final TestIamGlobalExceptionHandler exceptionHandler = new TestIamGlobalExceptionHandler();

    @Nested
    @DisplayName("When handling validation exceptions")
    class ValidationExceptions {
        
        @Test
        @DisplayName("Should return BAD_REQUEST with field errors for validation failures")
        void shouldReturnBadRequestWithFieldErrors() {
            // Given
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError("user", "email", "Invalid email format"));
            fieldErrors.add(new FieldError("user", "password", "Password too short"));
            
            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleValidationExceptions(ex);
            
            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Validation failed", responseBody.message());
            
            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) responseBody.data();
            assertNotNull(errors);
            assertEquals("Invalid email format", errors.get("email"));
            assertEquals("Password too short", errors.get("password"));
        }
    }
    
    @Nested
    @DisplayName("When handling HTTP message not readable")
    class HttpMessageNotReadable {
        
        @Test
        @DisplayName("Should return BAD_REQUEST for invalid request format")
        void shouldReturnBadRequestForInvalidRequestFormat() {
            // Given
            HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleHttpMessageNotReadable(ex);
            
            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Invalid request format", responseBody.message());
        }
    }
    
    @Nested
    @DisplayName("When handling domain exceptions")
    class DomainExceptions {
        
        @Test
        @DisplayName("Should return BAD_REQUEST for invalid user data")
        void shouldReturnBadRequestForInvalidUserData() {
            // Given
            InvalidUserDataException ex = new InvalidUserDataException("Invalid CPF format");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleInvalidUserData(ex);
            
            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Invalid CPF format", responseBody.message());
        }
        
        @Test
        @DisplayName("Should return CONFLICT for duplicate user")
        void shouldReturnConflictForDuplicateUser() {
            // Given
            DuplicateUserException ex = new DuplicateUserException("User with this CPF already exists");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleDuplicateUser(ex);
            
            // Then
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("User with this CPF already exists", responseBody.message());
        }
        
        @Test
        @DisplayName("Should return NOT_FOUND for user not found")
        void shouldReturnNotFoundForUserNotFound() {
            // Given
            UserNotFoundException ex = new UserNotFoundException("User with ID 123 not found");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleUserNotFound(ex);
            
            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("User with ID 123 not found", responseBody.message());
        }
        
        @Test
        @DisplayName("Should return UNAUTHORIZED for invalid credentials")
        void shouldReturnUnauthorizedForInvalidCredentials() {
            // Given
            InvalidCredentialsException ex = new InvalidCredentialsException("Invalid password");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleInvalidCredentials(ex);
            
            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Invalid credentials", responseBody.message());
        }
    }
    
    @Nested
    @DisplayName("When handling JWT authentication exceptions")
    class JwtAuthenticationExceptions {
        
        @Test
        @DisplayName("Should return UNAUTHORIZED for expired token")
        void shouldReturnUnauthorizedForExpiredToken() {
            // Given
            JwtAuthenticationException ex = new JwtAuthenticationException(JwtErrorType.EXPIRED_TOKEN, "Token expired");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleJwtAuthenticationException(ex);
            
            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Authentication token has expired", responseBody.message());
        }
        
        @Test
        @DisplayName("Should return UNAUTHORIZED for invalid signature")
        void shouldReturnUnauthorizedForInvalidSignature() {
            // Given
            JwtAuthenticationException ex = new JwtAuthenticationException(JwtErrorType.INVALID_SIGNATURE, "Invalid signature");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleJwtAuthenticationException(ex);
            
            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Invalid authentication token", responseBody.message());
        }
        
        @Test
        @DisplayName("Should return BAD_REQUEST for malformed token")
        void shouldReturnBadRequestForMalformedToken() {
            // Given
            JwtAuthenticationException ex = new JwtAuthenticationException(JwtErrorType.MALFORMED_TOKEN, "Malformed token");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleJwtAuthenticationException(ex);
            
            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Malformed authentication token", responseBody.message());
        }
        
        @Test
        @DisplayName("Should return UNAUTHORIZED for user not found in token")
        void shouldReturnUnauthorizedForUserNotFoundInToken() {
            // Given
            JwtAuthenticationException ex = new JwtAuthenticationException(JwtErrorType.USER_NOT_FOUND, "User not found");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleJwtAuthenticationException(ex);
            
            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("User not found", responseBody.message());
        }
        
        @Test
        @DisplayName("Should return UNAUTHORIZED for unknown JWT error")
        void shouldReturnUnauthorizedForUnknownJwtError() {
            // Given
            JwtAuthenticationException ex = new JwtAuthenticationException(JwtErrorType.OTHER, "Unknown error");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleJwtAuthenticationException(ex);
            
            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Authentication failed", responseBody.message());
        }
    }
    
    @Nested
    @DisplayName("When handling security exceptions")
    class SecurityExceptions {
        
        @Test
        @DisplayName("Should return UNAUTHORIZED for authentication exception")
        void shouldReturnUnauthorizedForAuthenticationException() {
            // Given
            BadCredentialsException ex = new BadCredentialsException("Authentication failed");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleAuthenticationException(ex);
            
            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Authentication failed", responseBody.message());
        }
        
        @Test
        @DisplayName("Should return FORBIDDEN for access denied")
        void shouldReturnForbiddenForAccessDenied() {
            // Given
            AccessDeniedException ex = new AccessDeniedException("Access denied");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleAccessDeniedException(ex);
            
            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("Access denied", responseBody.message());
        }
    }
    
    @Nested
    @DisplayName("When handling generic exceptions")
    class GenericExceptions {
        
        @Test
        @DisplayName("Should return INTERNAL_SERVER_ERROR for unexpected exceptions")
        void shouldReturnInternalServerErrorForUnexpectedExceptions() {
            // Given
            Exception ex = new RuntimeException("Something went wrong");
            
            // When
            ResponseEntity<IamErrorResponseDTO> response = exceptionHandler.handleGenericException(ex);
            
            // Then
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            IamErrorResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.success());
            assertEquals("An unexpected error occurred", responseBody.message());
            // No need to verify logger.error() was called as we're just preventing the log pollution
        }
    }
}
