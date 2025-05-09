package com.snackbar.iam.domain.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidUserDataExceptionTest {

    private static final String ERROR_MESSAGE = "Invalid user data: Email is required";

    @Test
    void shouldCreateExceptionWithSpecifiedMessage() {
        // When
        InvalidUserDataException exception = new InvalidUserDataException(ERROR_MESSAGE);
        
        // Then
        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void shouldInheritFromRuntimeException() {
        // When
        InvalidUserDataException exception = new InvalidUserDataException(ERROR_MESSAGE);
        
        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}
