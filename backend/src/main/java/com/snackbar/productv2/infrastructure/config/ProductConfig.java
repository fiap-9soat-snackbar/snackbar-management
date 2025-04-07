package com.snackbar.productv2.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.snackbar.productv2.application.gateways.Productv2Gateway;
import com.snackbar.productv2.application.usecases.*;
import com.snackbar.productv2.infrastructure.controllers.Productv2DTOMapper;
import com.snackbar.productv2.infrastructure.gateways.Productv2EntityMapper;
import com.snackbar.productv2.infrastructure.gateways.Productv2RepositoryGateway;
import com.snackbar.productv2.infrastructure.persistence.Productv2Repository;

@Configuration
public class ProductConfig {
    @Bean
    CreateProductv2UseCase createv2ProductUseCase(Productv2Gateway productv2Gateway) {
        return new CreateProductv2UseCase(productv2Gateway);
    }

    @Bean
    GetProductv2ByIdUseCase getProductv2ByIdUseCase(Productv2Gateway productv2Gateway) {
        return new GetProductv2ByIdUseCase(productv2Gateway);
    }
    
    @Bean
    ListProductsv2UseCase listProductsv2UseCase(Productv2Gateway productv2Gateway) {
        return new ListProductsv2UseCase(productv2Gateway);
    }

    @Bean
    GetProductsv2ByCategoryUseCase getProductsv2ByCategory(Productv2Gateway productv2Gateway) {
        return new GetProductsv2ByCategoryUseCase(productv2Gateway);
    }

    @Bean
    GetProductv2ByNameUseCase getProductv2ByNameUseCase(Productv2Gateway productv2Gateway) {
        return new GetProductv2ByNameUseCase(productv2Gateway);
    }

    @Bean
    UpdateProductv2ByIdUseCase updateProductv2ByIdUseCase(Productv2Gateway productv2Gateway, GetProductv2ByIdUseCase getProductv2ByIdUseCase) {
        return new UpdateProductv2ByIdUseCase(productv2Gateway, getProductv2ByIdUseCase);
    }

    @Bean
    DeleteProductv2ByIdUseCase deleteProductv2ByIdUseCase(Productv2Gateway productv2Gateway) {
        return new DeleteProductv2ByIdUseCase(productv2Gateway);
    }

    @Bean
    Productv2Gateway productv2Gateway(Productv2Repository productv2Repository, Productv2EntityMapper productv2EntityMapper) {
        return new Productv2RepositoryGateway(productv2Repository, productv2EntityMapper);
    }

    @Bean
    Productv2EntityMapper productv2EntityMapper() {
        return new Productv2EntityMapper();
    }

    @Bean
    Productv2DTOMapper productv2DTOMapper() {
        return new Productv2DTOMapper();
    }
}
