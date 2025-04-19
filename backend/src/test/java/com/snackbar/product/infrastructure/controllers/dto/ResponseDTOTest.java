package com.snackbar.product.infrastructure.controllers.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseDTOTest {

    @Test
    @DisplayName("Should create ResponseDTO with all properties")
    void constructor_ShouldCreateResponseDTOWithAllProperties() {
        // Given
        boolean success = true;
        String message = "Success message";
        Object data = "Test data";

        // When
        ResponseDTO responseDTO = new ResponseDTO(success, message, data);

        // Then
        assertTrue(responseDTO.success());
        assertEquals(message, responseDTO.message());
        assertEquals(data, responseDTO.data());
    }

    @Test
    @DisplayName("Should create ResponseDTO with success false")
    void constructor_ShouldCreateResponseDTOWithSuccessFalse() {
        // Given
        boolean success = false;
        String message = "Error message";
        Object data = null;

        // When
        ResponseDTO responseDTO = new ResponseDTO(success, message, data);

        // Then
        assertFalse(responseDTO.success());
        assertEquals(message, responseDTO.message());
        assertNull(responseDTO.data());
    }

    @Test
    @DisplayName("Should create ResponseDTO with complex data object")
    void constructor_ShouldCreateResponseDTOWithComplexDataObject() {
        // Given
        boolean success = true;
        String message = "Success message";
        TestObject data = new TestObject("test", 123);

        // When
        ResponseDTO responseDTO = new ResponseDTO(success, message, data);

        // Then
        assertTrue(responseDTO.success());
        assertEquals(message, responseDTO.message());
        assertEquals(data, responseDTO.data());
    }

    // Helper class for testing complex objects
    private static class TestObject {
        private final String name;
        private final int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
