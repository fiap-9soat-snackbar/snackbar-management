package com.snackbar.iam.domain.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidCredentialsExceptionTest {

    private static final String ERROR_MESSAGE = "Invalid username or password";

    @Test
    void shouldCreateExceptionWithSpecifiedMessage() {
        // When
        InvalidCredentialsException exception = new InvalidCredentialsException(ERROR_MESSAGE);
        
        // Then
        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void shouldInheritFromRuntimeException() {
        // When
        InvalidCredentialsException exception = new InvalidCredentialsException(ERROR_MESSAGE);
        
        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}
