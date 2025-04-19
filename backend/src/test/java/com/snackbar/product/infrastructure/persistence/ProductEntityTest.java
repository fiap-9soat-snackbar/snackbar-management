package com.snackbar.product.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductEntityTest {

    @Test
    @DisplayName("Should create ProductEntity with all properties")
    void constructor_ShouldCreateProductEntityWithAllProperties() {
        // Given
        String id = "1";
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description for product";
        BigDecimal price = new BigDecimal("10.99");
        Integer cookingTime = 5;

        // When
        ProductEntity entity = new ProductEntity(id, name, category, description, price, cookingTime);

        // Then
        assertEquals(id, entity.getId());
        assertEquals(name, entity.getName());
        assertEquals(category, entity.getCategory());
        assertEquals(description, entity.getDescription());
        assertEquals(price, entity.getPrice());
        assertEquals(cookingTime, entity.getCookingTime());
    }

    @Test
    @DisplayName("Should set and get id")
    void setAndGetId_ShouldWorkCorrectly() {
        // Given
        ProductEntity entity = new ProductEntity(null, "Test", "Lanche", "Description", BigDecimal.ONE, 1);
        String id = "new-id";

        // When
        entity.setId(id);

        // Then
        assertEquals(id, entity.getId());
    }

    @Test
    @DisplayName("Should set and get name")
    void setAndGetName_ShouldWorkCorrectly() {
        // Given
        ProductEntity entity = new ProductEntity("1", null, "Lanche", "Description", BigDecimal.ONE, 1);
        String name = "New Name";

        // When
        entity.setName(name);

        // Then
        assertEquals(name, entity.getName());
    }

    @Test
    @DisplayName("Should set and get category")
    void setAndGetCategory_ShouldWorkCorrectly() {
        // Given
        ProductEntity entity = new ProductEntity("1", "Test", null, "Description", BigDecimal.ONE, 1);
        String category = "Bebida";

        // When
        entity.setCategory(category);

        // Then
        assertEquals(category, entity.getCategory());
    }

    @Test
    @DisplayName("Should set and get description")
    void setAndGetDescription_ShouldWorkCorrectly() {
        // Given
        ProductEntity entity = new ProductEntity("1", "Test", "Lanche", null, BigDecimal.ONE, 1);
        String description = "New description";

        // When
        entity.setDescription(description);

        // Then
        assertEquals(description, entity.getDescription());
    }

    @Test
    @DisplayName("Should set and get price")
    void setAndGetPrice_ShouldWorkCorrectly() {
        // Given
        ProductEntity entity = new ProductEntity("1", "Test", "Lanche", "Description", null, 1);
        BigDecimal price = new BigDecimal("15.99");

        // When
        entity.setPrice(price);

        // Then
        assertEquals(price, entity.getPrice());
    }

    @Test
    @DisplayName("Should set and get cookingTime")
    void setAndGetCookingTime_ShouldWorkCorrectly() {
        // Given
        ProductEntity entity = new ProductEntity("1", "Test", "Lanche", "Description", BigDecimal.ONE, null);
        Integer cookingTime = 10;

        // When
        entity.setCookingTime(cookingTime);

        // Then
        assertEquals(cookingTime, entity.getCookingTime());
    }
}
