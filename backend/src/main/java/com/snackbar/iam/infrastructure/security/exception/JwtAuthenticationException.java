package com.snackbar.iam.infrastructure.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception for JWT authentication failures.
 * Extends Spring Security's AuthenticationException for better integration with the security framework.
 */
public class JwtAuthenticationException extends AuthenticationException {
    
    private final JwtErrorType errorType;
    
    /**
     * Creates a new JWT authentication exception with the specified error type.
     *
     * @param errorType The type of JWT error
     * @param message A detailed message for logging purposes
     */
    public JwtAuthenticationException(JwtErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }
    
    /**
     * Gets the type of JWT error.
     *
     * @return The JWT error type
     */
    public JwtErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Enum representing different types of JWT authentication errors.
     * This allows for more specific error handling based on the type of failure.
     */
    public enum JwtErrorType {
        /**
         * The JWT token has expired.
         */
        EXPIRED_TOKEN,
        
        /**
         * The JWT token signature is invalid.
         */
        INVALID_SIGNATURE,
        
        /**
         * The JWT token is malformed or cannot be parsed.
         */
        MALFORMED_TOKEN,
        
        /**
         * The user specified in the JWT token does not exist.
         */
        USER_NOT_FOUND,
        
        /**
         * Any other JWT-related error.
         */
        OTHER
    }
}
