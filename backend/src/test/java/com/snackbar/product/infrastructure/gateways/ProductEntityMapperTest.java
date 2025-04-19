package com.snackbar.product.infrastructure.gateways;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.persistence.ProductEntity;

class ProductEntityMapperTest {

    private ProductEntityMapper mapper;
    private Product product;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        mapper = new ProductEntityMapper();
        product = new Product("1", "Test Product", "Lanche", "Test description for product", new BigDecimal("10.99"), 5);
        productEntity = new ProductEntity("1", "Test Product", "Lanche", "Test description for product", new BigDecimal("10.99"), 5);
    }

    @Test
    @DisplayName("Should convert Product to ProductEntity")
    void toEntity_ShouldConvertProductToProductEntity() {
        // When
        ProductEntity result = mapper.toEntity(product);

        // Then
        assertEquals(product.id(), result.getId());
        assertEquals(product.name(), result.getName());
        assertEquals(product.category(), result.getCategory());
        assertEquals(product.description(), result.getDescription());
        assertEquals(product.price(), result.getPrice());
        assertEquals(product.cookingTime(), result.getCookingTime());
    }

    @Test
    @DisplayName("Should return null when Product is null")
    void toEntity_ShouldReturnNullWhenProductIsNull() {
        // When
        ProductEntity result = mapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert ProductEntity to Product")
    void toDomainObj_ShouldConvertProductEntityToProduct() {
        // When
        Product result = mapper.toDomainObj(productEntity);

        // Then
        assertEquals(productEntity.getId(), result.id());
        assertEquals(productEntity.getName(), result.name());
        assertEquals(productEntity.getCategory(), result.category());
        assertEquals(productEntity.getDescription(), result.description());
        assertEquals(productEntity.getPrice(), result.price());
        assertEquals(productEntity.getCookingTime(), result.cookingTime());
    }

    @Test
    @DisplayName("Should return null when ProductEntity is null")
    void toDomainObj_ShouldReturnNullWhenProductEntityIsNull() {
        // When
        Product result = mapper.toDomainObj(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert List of ProductEntity to List of Product")
    void toDomainListObj_ShouldConvertListOfProductEntityToListOfProduct() {
        // Given
        ProductEntity productEntity2 = new ProductEntity("2", "Another Product", "Bebida", "Another test description", new BigDecimal("5.99"), 2);
        List<ProductEntity> productEntities = Arrays.asList(productEntity, productEntity2);

        // When
        List<Product> results = mapper.toDomainListObj(productEntities);

        // Then
        assertEquals(2, results.size());
        
        assertEquals(productEntity.getId(), results.get(0).id());
        assertEquals(productEntity.getName(), results.get(0).name());
        assertEquals(productEntity.getCategory(), results.get(0).category());
        assertEquals(productEntity.getDescription(), results.get(0).description());
        assertEquals(productEntity.getPrice(), results.get(0).price());
        assertEquals(productEntity.getCookingTime(), results.get(0).cookingTime());
        
        assertEquals(productEntity2.getId(), results.get(1).id());
        assertEquals(productEntity2.getName(), results.get(1).name());
        assertEquals(productEntity2.getCategory(), results.get(1).category());
        assertEquals(productEntity2.getDescription(), results.get(1).description());
        assertEquals(productEntity2.getPrice(), results.get(1).price());
        assertEquals(productEntity2.getCookingTime(), results.get(1).cookingTime());
    }

    @Test
    @DisplayName("Should return empty list when ProductEntity list is null")
    void toDomainListObj_ShouldReturnEmptyListWhenProductEntityListIsNull() {
        // When
        List<Product> results = mapper.toDomainListObj(null);

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should handle empty list of ProductEntity")
    void toDomainListObj_ShouldHandleEmptyListOfProductEntity() {
        // When
        List<Product> results = mapper.toDomainListObj(Collections.emptyList());

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
