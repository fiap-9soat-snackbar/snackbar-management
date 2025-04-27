package com.snackbar.product.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.application.usecases.*;
import com.snackbar.product.infrastructure.controllers.ProductDTOMapper;
import com.snackbar.product.infrastructure.gateways.ProductEntityMapper;
import com.snackbar.product.infrastructure.gateways.ProductRepositoryGateway;
import com.snackbar.product.infrastructure.messaging.event.NoOpDomainEventPublisher;
import com.snackbar.product.infrastructure.persistence.ProductRepository;

@Configuration
public class ProductConfig {
    @Bean
    CreateProductUseCase createProductUseCase(ProductGateway productGateway, DomainEventPublisher eventPublisher) {
        return new CreateProductUseCase(productGateway, eventPublisher);
    }

    @Bean
    GetProductByIdUseCase getProductByIdUseCase(ProductGateway productGateway) {
        return new GetProductByIdUseCase(productGateway);
    }
    
    @Bean
    ListProductUseCase listProductUseCase(ProductGateway productGateway) {
        return new ListProductUseCase(productGateway);
    }
    
    @Bean
    GetProductByCategoryUseCase getProductByCategoryUseCase(ProductGateway productGateway) {
        return new GetProductByCategoryUseCase(productGateway);
    }
    
    @Bean
    GetProductByNameUseCase getProductByNameUseCase(ProductGateway productGateway) {
        return new GetProductByNameUseCase(productGateway);
    }
    
    @Bean
    UpdateProductByIdUseCase updateProductByIdUseCase(ProductGateway productGateway, GetProductByIdUseCase getProductByIdUseCase, DomainEventPublisher eventPublisher) {
        return new UpdateProductByIdUseCase(productGateway, getProductByIdUseCase, eventPublisher);
    }

    @Bean
    DeleteProductByIdUseCase deleteProductByIdUseCase(ProductGateway productGateway, DomainEventPublisher eventPublisher) {
        return new DeleteProductByIdUseCase(productGateway, eventPublisher);
    }

    @Bean
    ProductGateway productGateway(ProductRepository productRepository, ProductEntityMapper productEntityMapper) {
        return new ProductRepositoryGateway(productRepository, productEntityMapper);
    }

    @Bean
    ProductEntityMapper productEntityMapper() {
        return new ProductEntityMapper();
    }

    @Bean
    ProductDTOMapper productDTOMapper() {
        return new ProductDTOMapper();
    }
    
    /**
     * Fallback implementation of DomainEventPublisher that does nothing.
     * This will be used if the SQSDomainEventPublisher fails to initialize.
     */
    @Bean
    @Primary
    DomainEventPublisher noOpDomainEventPublisher() {
        return new NoOpDomainEventPublisher();
    }
}
