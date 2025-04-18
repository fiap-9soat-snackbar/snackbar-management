package com.snackbar.product.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.usecases.*;
import com.snackbar.product.infrastructure.controllers.ProductDTOMapper;
import com.snackbar.product.infrastructure.gateways.ProductEntityMapper;
import com.snackbar.product.infrastructure.gateways.ProductRepositoryGateway;
import com.snackbar.product.infrastructure.persistence.ProductRepository;

@Configuration
public class ProductConfig {
    @Bean
    CreateProductUseCase createProductUseCase(ProductGateway productGateway) {
        return new CreateProductUseCase(productGateway);
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
    GetProductByCategoryUseCase getProductByCategory(ProductGateway productGateway) {
        return new GetProductByCategoryUseCase(productGateway);
    }

    @Bean
    GetProductByNameUseCase getProductByNameUseCase(ProductGateway productGateway) {
        return new GetProductByNameUseCase(productGateway);
    }

    @Bean
    UpdateProductByIdUseCase updateProductByIdUseCase(ProductGateway productGateway, GetProductByIdUseCase getProductByIdUseCase) {
        return new UpdateProductByIdUseCase(productGateway, getProductByIdUseCase);
    }

    @Bean
    DeleteProductByIdUseCase deleteProductByIdUseCase(ProductGateway productGateway) {
        return new DeleteProductByIdUseCase(productGateway);
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
}
