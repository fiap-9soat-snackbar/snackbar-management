package com.snackbar.iam.infrastructure.controllers;

import com.snackbar.iam.domain.exceptions.DuplicateUserException;
import com.snackbar.iam.domain.exceptions.InvalidCredentialsException;
import com.snackbar.iam.domain.exceptions.InvalidUserDataException;
import com.snackbar.iam.domain.exceptions.UserNotFoundException;
import com.snackbar.iam.infrastructure.controllers.dto.IamErrorResponseDTO;
import com.snackbar.iam.infrastructure.security.exception.JwtAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for IAM module controllers.
 * Provides consistent error responses across all IAM endpoints.
 * Named with "Iam" prefix to avoid bean naming conflicts with other modules.
 */
@RestControllerAdvice
public class IamGlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(IamGlobalExceptionHandler.class);
    
    /**
     * Handles validation exceptions.
     *
     * @param ex The validation exception
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<IamErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.warn("Validation error: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new IamErrorResponseDTO(false, "Validation failed", errors));
    }
    
    /**
     * Handles invalid JSON request body.
     *
     * @param ex The exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<IamErrorResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Invalid request body: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(IamErrorResponseDTO.error("Invalid request format"));
    }
    
    /**
     * Handles invalid user data exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<IamErrorResponseDTO> handleInvalidUserData(InvalidUserDataException ex) {
        logger.warn("Invalid user data: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(IamErrorResponseDTO.error(ex.getMessage()));
    }
    
    /**
     * Handles duplicate user exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<IamErrorResponseDTO> handleDuplicateUser(DuplicateUserException ex) {
        logger.warn("Duplicate user: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(IamErrorResponseDTO.error(ex.getMessage()));
    }
    
    /**
     * Handles user not found exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<IamErrorResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        logger.warn("User not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(IamErrorResponseDTO.error(ex.getMessage()));
    }
    
    /**
     * Handles invalid credentials exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<IamErrorResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        logger.warn("Invalid credentials: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(IamErrorResponseDTO.error("Invalid credentials"));
    }
    
    /**
     * Handles JWT authentication exceptions with specific error types.
     *
     * @param ex The JWT authentication exception
     * @return ResponseEntity with appropriate status code and error message
     */
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<IamErrorResponseDTO> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        HttpStatus status;
        String message;
        
        // Determine appropriate status code and message based on error type
        switch (ex.getErrorType()) {
            case EXPIRED_TOKEN:
                status = HttpStatus.UNAUTHORIZED;
                message = "Authentication token has expired";
                logger.warn("JWT token expired: {}", ex.getMessage());
                break;
            case INVALID_SIGNATURE:
                status = HttpStatus.UNAUTHORIZED;
                message = "Invalid authentication token";
                logger.warn("JWT signature invalid: {}", ex.getMessage());
                break;
            case MALFORMED_TOKEN:
                status = HttpStatus.BAD_REQUEST;
                message = "Malformed authentication token";
                logger.warn("JWT token malformed: {}", ex.getMessage());
                break;
            case USER_NOT_FOUND:
                status = HttpStatus.UNAUTHORIZED;
                message = "User not found";
                logger.warn("JWT token for non-existent user: {}", ex.getMessage());
                break;
            default:
                status = HttpStatus.UNAUTHORIZED;
                message = "Authentication failed";
                logger.warn("JWT authentication failed: {}", ex.getMessage());
        }
        
        return ResponseEntity
                .status(status)
                .body(IamErrorResponseDTO.error(message));
    }
    
    /**
     * Handles general Spring Security authentication exceptions.
     *
     * @param ex The authentication exception
     * @return ResponseEntity with unauthorized status
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<IamErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(IamErrorResponseDTO.error("Authentication failed"));
    }
    
    /**
     * Handles access denied exceptions.
     *
     * @param ex The access denied exception
     * @return ResponseEntity with forbidden status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<IamErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(IamErrorResponseDTO.error("Access denied"));
    }
    
    /**
     * Handles all other exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<IamErrorResponseDTO> handleGenericException(Exception ex) {
        logger.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(IamErrorResponseDTO.error("An unexpected error occurred"));
    }
}
