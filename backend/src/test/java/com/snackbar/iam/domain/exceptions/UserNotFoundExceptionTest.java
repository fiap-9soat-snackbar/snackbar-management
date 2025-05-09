package com.snackbar.iam.domain.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {

    private static final String ERROR_MESSAGE = "User not found with id: 123";

    @Test
    void shouldCreateExceptionWithSpecifiedMessage() {
        // When
        UserNotFoundException exception = new UserNotFoundException(ERROR_MESSAGE);
        
        // Then
        assertEquals(ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void shouldInheritFromRuntimeException() {
        // When
        UserNotFoundException exception = new UserNotFoundException(ERROR_MESSAGE);
        
        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}
