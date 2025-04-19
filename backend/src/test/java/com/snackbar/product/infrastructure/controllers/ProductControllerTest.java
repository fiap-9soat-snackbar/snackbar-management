package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.snackbar.product.application.usecases.CreateProductUseCase;
import com.snackbar.product.application.usecases.DeleteProductByIdUseCase;
import com.snackbar.product.application.usecases.GetProductByCategoryUseCase;
import com.snackbar.product.application.usecases.GetProductByIdUseCase;
import com.snackbar.product.application.usecases.GetProductByNameUseCase;
import com.snackbar.product.application.usecases.ListProductUseCase;
import com.snackbar.product.application.usecases.UpdateProductByIdUseCase;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.controllers.dto.ResponseDTO;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private GetProductByIdUseCase getProductByIdUseCase;

    @Mock
    private GetProductByNameUseCase getProductByNameUseCase;

    @Mock
    private GetProductByCategoryUseCase getProductByCategoryUseCase;

    @Mock
    private ListProductUseCase listProductUseCase;

    @Mock
    private UpdateProductByIdUseCase updateProductByIdUseCase;

    @Mock
    private DeleteProductByIdUseCase deleteProductByIdUseCase;

    @Mock
    private ProductDTOMapper productDTOMapper;

    @InjectMocks
    private ProductController productController;

    private Product product;
    private CreateProductRequest createRequest;
    private CreateProductResponse createResponse;
    private GetProductResponse getResponse;

    @BeforeEach
    void setUp() {
        product = new Product("1", "Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        createRequest = new CreateProductRequest("Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        createResponse = new CreateProductResponse("1", "Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        getResponse = new GetProductResponse("1", "Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
    }

    @Test
    @DisplayName("Should create product")
    void createProduct_ShouldCreateProduct() {
        // Given
        when(productDTOMapper.createRequestToDomain(createRequest)).thenReturn(product);
        when(createProductUseCase.createProduct(product)).thenReturn(product);
        when(productDTOMapper.createToResponse(product)).thenReturn(createResponse);

        // When
        ResponseEntity<ResponseDTO> response = productController.createProduct(createRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertEquals("Product created successfully", response.getBody().message());
        assertEquals(createResponse, response.getBody().data());
    }

    @Test
    @DisplayName("Should get product by id")
    void getProductById_ShouldGetProductById() {
        // Given
        when(getProductByIdUseCase.getProductById("1")).thenReturn(product);
        when(productDTOMapper.getToResponse(product)).thenReturn(getResponse);

        // When
        ResponseEntity<ResponseDTO> response = productController.getProductById("1");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertEquals("Product retrieved successfully", response.getBody().message());
        assertEquals(getResponse, response.getBody().data());
    }

    @Test
    @DisplayName("Should get product by name")
    void getProductByName_ShouldGetProductByName() {
        // Given
        when(getProductByNameUseCase.getProductByName("Test Product")).thenReturn(product);
        when(productDTOMapper.getToResponse(product)).thenReturn(getResponse);

        // When
        ResponseEntity<ResponseDTO> response = productController.getProductByName("Test Product");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertEquals("Product retrieved successfully", response.getBody().message());
        assertEquals(getResponse, response.getBody().data());
    }

    @Test
    @DisplayName("Should get products by category")
    void getProductByCategory_ShouldGetProductsByCategory() {
        // Given
        List<Product> products = Arrays.asList(product);
        List<GetProductResponse> responses = Arrays.asList(getResponse);
        
        when(getProductByCategoryUseCase.getProductByCategory("Lanche")).thenReturn(products);
        when(productDTOMapper.listToResponse(products)).thenReturn(responses);

        // When
        ResponseEntity<ResponseDTO> response = productController.getProductByCategory("Lanche");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertEquals("Products retrieved successfully", response.getBody().message());
        assertEquals(responses, response.getBody().data());
    }

    @Test
    @DisplayName("Should list all products")
    void listProduct_ShouldListAllProducts() {
        // Given
        List<Product> products = Arrays.asList(product);
        List<GetProductResponse> responses = Arrays.asList(getResponse);
        
        when(listProductUseCase.listProduct()).thenReturn(products);
        when(productDTOMapper.listToResponse(products)).thenReturn(responses);

        // When
        ResponseEntity<ResponseDTO> response = productController.listProduct();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertEquals("Products retrieved successfully", response.getBody().message());
        assertEquals(responses, response.getBody().data());
    }

    @Test
    @DisplayName("Should update product")
    void updateProductById_ShouldUpdateProduct() {
        // Given
        Product updatedProduct = new Product("1", "Updated Product", "Bebida", "Updated description", new BigDecimal("15.99"), 10);
        CreateProductRequest updateRequest = new CreateProductRequest("Updated Product", "Bebida", "Updated description", new BigDecimal("15.99"), 10);
        CreateProductResponse updatedResponse = new CreateProductResponse("1", "Updated Product", "Bebida", "Updated description", new BigDecimal("15.99"), 10);
        
        when(productDTOMapper.createRequestToDomain(updateRequest)).thenReturn(updatedProduct);
        when(updateProductByIdUseCase.updateProductById("1", updatedProduct)).thenReturn(updatedProduct);
        when(productDTOMapper.createToResponse(updatedProduct)).thenReturn(updatedResponse);

        // When
        ResponseEntity<ResponseDTO> response = productController.updateProductById("1", updateRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertEquals("Product updated successfully", response.getBody().message());
        assertEquals(updatedResponse, response.getBody().data());
    }

    @Test
    @DisplayName("Should delete product")
    void deleteProduct_ShouldDeleteProduct() {
        // Given
        doNothing().when(deleteProductByIdUseCase).deleteProductById(anyString());

        // When
        ResponseEntity<ResponseDTO> response = productController.deleteProduct("1");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().success());
        assertEquals("Product deleted successfully", response.getBody().message());
        verify(deleteProductByIdUseCase, times(1)).deleteProductById("1");
    }
}
