package com.snackbar.product.infrastructure.controllers;

import java.util.List;

import com.snackbar.product.domain.entity.Product;

public class ProductDTOMapper {
    CreateProductResponse createToResponse(Product product) {
        if (product == null) {
            return null;
        }
        return new CreateProductResponse(product.id(), product.name(), product.category(), product.description(), product.price(), product.cookingTime());
    }

    public Product createRequestToDomain(CreateProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Product request cannot be null");
        }
        return new Product(null, request.name(), request.category(), request.description(), request.price(), request.cookingTime());
    }

    GetProductResponse getToResponse(Product product) {
        if (product == null) {
            return null;
        }
        return new GetProductResponse(product.id(), product.name(), product.category(), product.description(), product.price(), product.cookingTime());
    }
    
    List<GetProductResponse> listToResponse(List<Product> listProduct) {
        if (listProduct == null) {
            return List.of();
        }
        return listProduct.stream()
            .map(this::getToResponse)
            .filter(response -> response != null)  // Filter out any nulls from getToResponse
            .toList();
    }
    
}
