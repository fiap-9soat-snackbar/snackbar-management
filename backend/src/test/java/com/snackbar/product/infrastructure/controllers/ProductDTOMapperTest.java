package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.snackbar.product.domain.entity.Product;

class ProductDTOMapperTest {

    private ProductDTOMapper mapper;
    private Product product;
    private CreateProductRequest request;

    @BeforeEach
    void setUp() {
        mapper = new ProductDTOMapper();
        product = new Product("1", "Test Product", "Lanche", "Test description for product", new BigDecimal("10.99"), 5);
        request = new CreateProductRequest("Test Product", "Lanche", "Test description for product", new BigDecimal("10.99"), 5);
    }

    @Test
    @DisplayName("Should convert Product to CreateProductResponse")
    void createToResponse_ShouldConvertProductToCreateProductResponse() {
        // When
        CreateProductResponse response = mapper.createToResponse(product);

        // Then
        assertEquals(product.id(), response.id());
        assertEquals(product.name(), response.name());
        assertEquals(product.category(), response.category());
        assertEquals(product.description(), response.description());
        assertEquals(product.price(), response.price());
        assertEquals(product.cookingTime(), response.cookingTime());
    }

    @Test
    @DisplayName("Should convert CreateProductRequest to Product")
    void createRequestToDomain_ShouldConvertCreateProductRequestToProduct() {
        // When
        Product result = mapper.createRequestToDomain(request);

        // Then
        assertNull(result.id());
        assertEquals(request.name(), result.name());
        assertEquals(request.category(), result.category());
        assertEquals(request.description(), result.description());
        assertEquals(request.price(), result.price());
        assertEquals(request.cookingTime(), result.cookingTime());
    }

    @Test
    @DisplayName("Should convert Product to GetProductResponse")
    void getToResponse_ShouldConvertProductToGetProductResponse() {
        // When
        GetProductResponse response = mapper.getToResponse(product);

        // Then
        assertEquals(product.id(), response.id());
        assertEquals(product.name(), response.name());
        assertEquals(product.category(), response.category());
        assertEquals(product.description(), response.description());
        assertEquals(product.price(), response.price());
        assertEquals(product.cookingTime(), response.cookingTime());
    }

    @Test
    @DisplayName("Should convert List of Products to List of GetProductResponse")
    void listToResponse_ShouldConvertListOfProductsToListOfGetProductResponse() {
        // Given
        Product product2 = new Product("2", "Another Product", "Bebida", "Another test description", new BigDecimal("5.99"), 2);
        List<Product> products = Arrays.asList(product, product2);

        // When
        List<GetProductResponse> responses = mapper.listToResponse(products);

        // Then
        assertEquals(2, responses.size());
        
        assertEquals(product.id(), responses.get(0).id());
        assertEquals(product.name(), responses.get(0).name());
        assertEquals(product.category(), responses.get(0).category());
        assertEquals(product.description(), responses.get(0).description());
        assertEquals(product.price(), responses.get(0).price());
        assertEquals(product.cookingTime(), responses.get(0).cookingTime());
        
        assertEquals(product2.id(), responses.get(1).id());
        assertEquals(product2.name(), responses.get(1).name());
        assertEquals(product2.category(), responses.get(1).category());
        assertEquals(product2.description(), responses.get(1).description());
        assertEquals(product2.price(), responses.get(1).price());
        assertEquals(product2.cookingTime(), responses.get(1).cookingTime());
    }
}
