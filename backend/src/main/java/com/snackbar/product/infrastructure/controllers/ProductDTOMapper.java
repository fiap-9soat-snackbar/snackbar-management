package com.snackbar.product.infrastructure.controllers;

import java.util.List;

import com.snackbar.product.domain.entity.Product;

public class ProductDTOMapper {
    CreateProductResponse createToResponse(Product product) {
        return new CreateProductResponse(product.id(), product.name(), product.category(), product.description(), product.price(), product.cookingTime());
    }

    public Product createRequestToDomain(CreateProductRequest request) {
        return new Product(null, request.name(), request.category(), request.description(), request.price(), request.cookingTime());
    }

    GetProductResponse getToResponse(Product product) {
        return new GetProductResponse(product.id(), product.name(), product.category(), product.description(), product.price(), product.cookingTime());
    }
    
    List<GetProductResponse> listToResponse(List<Product> listProduct) {
        return listProduct.stream()
            .map(this::getToResponse)
            .toList();
    }
    
}
