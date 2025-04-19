package com.snackbar.product.infrastructure.gateways;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import com.snackbar.product.infrastructure.persistence.ProductEntity;
import com.snackbar.product.infrastructure.persistence.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryGatewayTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @InjectMocks
    private ProductRepositoryGateway productRepositoryGateway;

    private Product product;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        product = new Product("1", "Test Product", "Lanche", "Test description for product", new BigDecimal("10.99"), 5);
        productEntity = new ProductEntity("1", "Test Product", "Lanche", "Test description for product", new BigDecimal("10.99"), 5);
    }

    @Test
    @DisplayName("Should create product")
    void createProduct_ShouldCreateProduct() {
        // Given
        when(productEntityMapper.toEntity(product)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productEntityMapper.toDomainObj(productEntity)).thenReturn(product);

        // When
        Product result = productRepositoryGateway.createProduct(product);

        // Then
        assertNotNull(result);
        assertEquals(product.id(), result.id());
        assertEquals(product.name(), result.name());
        verify(productRepository, times(1)).save(productEntity);
    }

    @Test
    @DisplayName("Should find product by id")
    void getProductById_ShouldFindProductById() {
        // Given
        when(productRepository.findById("1")).thenReturn(Optional.of(productEntity));
        when(productEntityMapper.toDomainObj(productEntity)).thenReturn(product);

        // When
        Product result = productRepositoryGateway.getProductById("1");

        // Then
        assertNotNull(result);
        assertEquals(product.id(), result.id());
        assertEquals(product.name(), result.name());
    }

    @Test
    @DisplayName("Should throw exception when product not found by id")
    void getProductById_ShouldThrowExceptionWhenProductNotFoundById() {
        // Given
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ProductNotFoundException.class, () -> productRepositoryGateway.getProductById("999"));
    }

    @Test
    @DisplayName("Should find product by name")
    void getProductByName_ShouldFindProductByName() {
        // Given
        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(productEntity));
        when(productEntityMapper.toDomainObj(productEntity)).thenReturn(product);

        // When
        Product result = productRepositoryGateway.getProductByName("Test Product");

        // Then
        assertNotNull(result);
        assertEquals(product.id(), result.id());
        assertEquals(product.name(), result.name());
    }

    @Test
    @DisplayName("Should throw exception when product not found by name")
    void getProductByName_ShouldThrowExceptionWhenProductNotFoundByName() {
        // Given
        when(productRepository.findByName("Nonexistent")).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ProductNotFoundException.class, () -> productRepositoryGateway.getProductByName("Nonexistent"));
    }

    @Test
    @DisplayName("Should find products by category")
    void getProductByCategory_ShouldFindProductsByCategory() {
        // Given
        List<ProductEntity> productEntities = Arrays.asList(productEntity);
        when(productRepository.findByCategory("Lanche")).thenReturn(productEntities);
        when(productEntityMapper.toDomainListObj(productEntities)).thenReturn(Arrays.asList(product));

        // When
        List<Product> results = productRepositoryGateway.getProductByCategory("Lanche");

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(product.id(), results.get(0).id());
        assertEquals(product.name(), results.get(0).name());
    }

    @Test
    @DisplayName("Should return empty list when no products found by category")
    void getProductByCategory_ShouldReturnEmptyListWhenNoProductsFoundByCategory() {
        // Given
        when(productRepository.findByCategory("NonexistentCategory")).thenReturn(Collections.emptyList());
        when(productEntityMapper.toDomainListObj(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<Product> results = productRepositoryGateway.getProductByCategory("NonexistentCategory");

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should list all products")
    void listProduct_ShouldListAllProducts() {
        // Given
        List<ProductEntity> productEntities = Arrays.asList(productEntity);
        when(productRepository.findAll()).thenReturn(productEntities);
        when(productEntityMapper.toDomainListObj(productEntities)).thenReturn(Arrays.asList(product));

        // When
        List<Product> results = productRepositoryGateway.listProduct();

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(product.id(), results.get(0).id());
        assertEquals(product.name(), results.get(0).name());
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void listProduct_ShouldReturnEmptyListWhenNoProductsExist() {
        // Given
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        when(productEntityMapper.toDomainListObj(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<Product> results = productRepositoryGateway.listProduct();

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should update product")
    void updateProductById_ShouldUpdateProduct() {
        // Given
        Product updatedProduct = new Product("1", "Updated Product", "Bebida", "Updated description for product", new BigDecimal("15.99"), 10);
        ProductEntity updatedEntity = new ProductEntity("1", "Updated Product", "Bebida", "Updated description for product", new BigDecimal("15.99"), 10);
        
        when(productRepository.existsById("1")).thenReturn(true);
        when(productEntityMapper.toEntity(updatedProduct)).thenReturn(updatedEntity);
        when(productRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(productEntityMapper.toDomainObj(updatedEntity)).thenReturn(updatedProduct);

        // When
        Product result = productRepositoryGateway.updateProductById("1", updatedProduct);

        // Then
        assertNotNull(result);
        assertEquals(updatedProduct.id(), result.id());
        assertEquals(updatedProduct.name(), result.name());
        assertEquals(updatedProduct.category(), result.category());
        assertEquals(updatedProduct.description(), result.description());
        assertEquals(updatedProduct.price(), result.price());
        assertEquals(updatedProduct.cookingTime(), result.cookingTime());
        verify(productRepository, times(1)).save(updatedEntity);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void updateProductById_ShouldThrowExceptionWhenUpdatingNonExistentProduct() {
        // Given
        Product nonExistentProduct = new Product("999", "Non-existent", "Lanche", "Description for product test", BigDecimal.ONE, 1);
        when(productRepository.existsById("999")).thenReturn(false);

        // When/Then
        assertThrows(ProductNotFoundException.class, () -> productRepositoryGateway.updateProductById("999", nonExistentProduct));
    }

    @Test
    @DisplayName("Should delete product by id")
    void deleteProductById_ShouldDeleteProductById() {
        // Given
        when(productRepository.findById("1")).thenReturn(Optional.of(productEntity));
        doNothing().when(productRepository).delete(productEntity);

        // When
        productRepositoryGateway.deleteProductById("1");

        // Then
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).delete(productEntity);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void deleteProductById_ShouldThrowExceptionWhenDeletingNonExistentProduct() {
        // Given
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ProductNotFoundException.class, () -> productRepositoryGateway.deleteProductById("999"));
        verify(productRepository, times(1)).findById("999");
        verify(productRepository, times(0)).delete(any(ProductEntity.class));
    }
}
