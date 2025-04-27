package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
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

    // We'll create a new controller instance manually instead of using @InjectMocks
    private ProductController productController;

    private Product product;
    private CreateProductRequest createRequest;
    private CreateProductResponse createResponse;
    private GetProductResponse getResponse;

    @BeforeEach
    void setUp() {
        // Create product test data
        product = new Product("1", "Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        createRequest = new CreateProductRequest("Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        createResponse = new CreateProductResponse("1", "Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        getResponse = new GetProductResponse("1", "Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        
        // Create a new controller instance with mocked dependencies
        productController = new ProductController(
            createProductUseCase,
            getProductByIdUseCase,
            listProductUseCase,
            getProductByCategoryUseCase,
            getProductByNameUseCase,
            updateProductByIdUseCase,
            deleteProductByIdUseCase,
            productDTOMapper
        );
        
        // Create a mock logger
        Logger mockLogger = mock(Logger.class);
        
        // Use reflection to set the logger field in the controller
        try {
            Field loggerField = ProductController.class.getDeclaredField("logger");
            loggerField.setAccessible(true);
            
            // Remove the final modifier
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(loggerField, loggerField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            
            // Set the mock logger
            loggerField.set(null, mockLogger);
        } catch (Exception e) {
            System.err.println("Failed to set mock logger: " + e.getMessage());
        }
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
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertTrue(responseBody.success(), "Response should indicate success");
        assertEquals("Product created successfully", responseBody.message(), "Response message should match");
        assertEquals(createResponse, responseBody.data(), "Response data should match");
    }

    @Test
    @DisplayName("Should return bad request when create product request is null")
    void createProduct_ShouldReturnBadRequestWhenRequestIsNull() {
        // When
        ResponseEntity<ResponseDTO> response = productController.createProduct(null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Request body cannot be null", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
    }

    @Test
    @DisplayName("Should handle exception when creating product")
    void createProduct_ShouldHandleException() {
        // Given
        RuntimeException testException = new RuntimeException("Test exception for testing");
        when(productDTOMapper.createRequestToDomain(createRequest)).thenReturn(product);
        when(createProductUseCase.createProduct(product)).thenThrow(testException);

        // When
        ResponseEntity<ResponseDTO> response = productController.createProduct(createRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Error creating product: Test exception for testing", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
    }

    @Test
    @DisplayName("Should handle null result from create product use case")
    void createProduct_ShouldHandleNullResult() {
        // Given
        when(productDTOMapper.createRequestToDomain(createRequest)).thenReturn(product);
        when(createProductUseCase.createProduct(product)).thenReturn(null);

        // When
        ResponseEntity<ResponseDTO> response = productController.createProduct(createRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Failed to create product", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
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
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertTrue(responseBody.success(), "Response should indicate success");
        assertEquals("Product retrieved successfully", responseBody.message(), "Response message should match");
        assertEquals(getResponse, responseBody.data(), "Response data should match");
    }

    @Test
    @DisplayName("Should return bad request when product id is null or empty")
    void getProductById_ShouldReturnBadRequestWhenIdIsNullOrEmpty() {
        // When
        ResponseEntity<ResponseDTO> response = productController.getProductById("");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Product ID cannot be null or empty", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
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
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertTrue(responseBody.success(), "Response should indicate success");
        assertEquals("Product retrieved successfully", responseBody.message(), "Response message should match");
        assertEquals(getResponse, responseBody.data(), "Response data should match");
    }

    @Test
    @DisplayName("Should return bad request when product name is null or empty")
    void getProductByName_ShouldReturnBadRequestWhenNameIsNullOrEmpty() {
        // When
        ResponseEntity<ResponseDTO> response = productController.getProductByName(null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Product name cannot be null or empty", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
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
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertTrue(responseBody.success(), "Response should indicate success");
        assertEquals("Products retrieved successfully", responseBody.message(), "Response message should match");
        assertEquals(responses, responseBody.data(), "Response data should match");
    }

    @Test
    @DisplayName("Should return bad request when product category is null or empty")
    void getProductByCategory_ShouldReturnBadRequestWhenCategoryIsNullOrEmpty() {
        // When
        ResponseEntity<ResponseDTO> response = productController.getProductByCategory("  ");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Product category cannot be null or empty", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
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
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertTrue(responseBody.success(), "Response should indicate success");
        assertEquals("Products retrieved successfully", responseBody.message(), "Response message should match");
        assertEquals(responses, responseBody.data(), "Response data should match");
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
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertTrue(responseBody.success(), "Response should indicate success");
        assertEquals("Product updated successfully", responseBody.message(), "Response message should match");
        assertEquals(updatedResponse, responseBody.data(), "Response data should match");
    }

    @Test
    @DisplayName("Should return bad request when update product id is null or empty")
    void updateProductById_ShouldReturnBadRequestWhenIdIsNullOrEmpty() {
        // When
        ResponseEntity<ResponseDTO> response = productController.updateProductById("", createRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Product ID cannot be null or empty", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
    }

    @Test
    @DisplayName("Should return bad request when update product request is null")
    void updateProductById_ShouldReturnBadRequestWhenRequestIsNull() {
        // When
        ResponseEntity<ResponseDTO> response = productController.updateProductById("1", null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Request body cannot be null", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
    }

    @Test
    @DisplayName("Should handle null result from update product use case")
    void updateProductById_ShouldHandleNullResult() {
        // Given
        when(productDTOMapper.createRequestToDomain(createRequest)).thenReturn(product);
        when(updateProductByIdUseCase.updateProductById("1", product)).thenReturn(null);

        // When
        ResponseEntity<ResponseDTO> response = productController.updateProductById("1", createRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Failed to update product", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
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
        assertNotNull(response.getBody(), "Response body should not be null");
        
        // Using Objects.requireNonNull to ensure the IDE knows responseBody is not null
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertTrue(responseBody.success(), "Response should indicate success");
        assertEquals("Product deleted successfully", responseBody.message(), "Response message should match");
        verify(deleteProductByIdUseCase, times(1)).deleteProductById("1");
    }

    @Test
    @DisplayName("Should return bad request when delete product id is null or empty")
    void deleteProduct_ShouldReturnBadRequestWhenIdIsNullOrEmpty() {
        // When
        ResponseEntity<ResponseDTO> response = productController.deleteProduct(null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody(), "Response body should not be null");
        
        ResponseDTO responseBody = Objects.requireNonNull(response.getBody());
        assertEquals(false, responseBody.success(), "Response should indicate failure");
        assertEquals("Product ID cannot be null or empty", responseBody.message(), "Response message should match");
        assertEquals(null, responseBody.data(), "Response data should be null");
    }
}
