package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetProductResponseTest {

    @Test
    @DisplayName("Should create GetProductResponse with all properties")
    void constructor_ShouldCreateGetProductResponseWithAllProperties() {
        // Given
        String id = "1";
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description for product";
        BigDecimal price = new BigDecimal("10.99");
        Integer cookingTime = 5;

        // When
        GetProductResponse response = new GetProductResponse(id, name, category, description, price, cookingTime);

        // Then
        assertEquals(id, response.id());
        assertEquals(name, response.name());
        assertEquals(category, response.category());
        assertEquals(description, response.description());
        assertEquals(price, response.price());
        assertEquals(cookingTime, response.cookingTime());
    }
}
