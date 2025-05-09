package com.snackbar.iam.domain.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DuplicateUserExceptionTest {

    private static final String ERROR_MESSAGE = "User with email user@example.com already exists";

    @Test
    void shouldCreateExceptionWithSpecifiedMessage() {
        // When
        DuplicateUserException exception = new DuplicateUserException(ERROR_MESSAGE);
        
        // Then
        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void shouldInheritFromRuntimeException() {
        // When
        DuplicateUserException exception = new DuplicateUserException(ERROR_MESSAGE);
        
        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}
