package com.snackbar.product.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.application.usecases.CreateProductUseCase;
import com.snackbar.product.application.usecases.DeleteProductByIdUseCase;
import com.snackbar.product.application.usecases.GetProductByCategoryUseCase;
import com.snackbar.product.application.usecases.GetProductByIdUseCase;
import com.snackbar.product.application.usecases.GetProductByNameUseCase;
import com.snackbar.product.application.usecases.ListProductUseCase;
import com.snackbar.product.application.usecases.UpdateProductByIdUseCase;
import com.snackbar.product.infrastructure.controllers.ProductDTOMapper;
import com.snackbar.product.infrastructure.gateways.ProductEntityMapper;
import com.snackbar.product.infrastructure.persistence.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductConfigTest {

    @InjectMocks
    private ProductConfig productConfig;

    @Mock
    private ProductGateway productGateway;

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private DomainEventPublisher eventPublisher;
    
    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("Should create CreateProductUseCase bean")
    void createProductUseCase_ShouldCreateBean() {
        // When
        CreateProductUseCase useCase = productConfig.createProductUseCase(productGateway, eventPublisher);

        // Then
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Should create GetProductByIdUseCase bean")
    void getProductByIdUseCase_ShouldCreateBean() {
        // When
        GetProductByIdUseCase useCase = productConfig.getProductByIdUseCase(productGateway);

        // Then
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Should create ListProductUseCase bean")
    void listProductUseCase_ShouldCreateBean() {
        // When
        ListProductUseCase useCase = productConfig.listProductUseCase(productGateway);

        // Then
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Should create GetProductByCategoryUseCase bean")
    void getProductByCategory_ShouldCreateBean() {
        // When
        GetProductByCategoryUseCase useCase = productConfig.getProductByCategory(productGateway);

        // Then
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Should create GetProductByNameUseCase bean")
    void getProductByNameUseCase_ShouldCreateBean() {
        // When
        GetProductByNameUseCase useCase = productConfig.getProductByNameUseCase(productGateway);

        // Then
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Should create UpdateProductByIdUseCase bean")
    void updateProductByIdUseCase_ShouldCreateBean() {
        // Given
        GetProductByIdUseCase getProductByIdUseCase = mock(GetProductByIdUseCase.class);

        // When
        UpdateProductByIdUseCase useCase = productConfig.updateProductByIdUseCase(productGateway, getProductByIdUseCase, eventPublisher);

        // Then
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Should create DeleteProductByIdUseCase bean")
    void deleteProductByIdUseCase_ShouldCreateBean() {
        // When
        DeleteProductByIdUseCase useCase = productConfig.deleteProductByIdUseCase(productGateway, eventPublisher);

        // Then
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Should create ProductGateway bean")
    void productGateway_ShouldCreateBean() {
        // Given
        ProductEntityMapper productEntityMapper = mock(ProductEntityMapper.class);

        // When
        ProductGateway gateway = productConfig.productGateway(productRepository, productEntityMapper, mongoTemplate);

        // Then
        assertNotNull(gateway);
    }

    @Test
    @DisplayName("Should create ProductEntityMapper bean")
    void productEntityMapper_ShouldCreateBean() {
        // When
        ProductEntityMapper mapper = productConfig.productEntityMapper();

        // Then
        assertNotNull(mapper);
    }

    @Test
    @DisplayName("Should create ProductDTOMapper bean")
    void productDTOMapper_ShouldCreateBean() {
        // When
        ProductDTOMapper mapper = productConfig.productDTOMapper();

        // Then
        assertNotNull(mapper);
    }
    
    @Test
    @DisplayName("Should create NoOpDomainEventPublisher bean")
    void noOpDomainEventPublisher_ShouldCreateBean() {
        // When
        DomainEventPublisher publisher = productConfig.noOpDomainEventPublisher();

        // Then
        assertNotNull(publisher);
    }
}
