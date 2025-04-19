package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;

public class GetProductByIdUseCase {
    
    private final ProductGateway productGateway;

    public GetProductByIdUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Product getProductById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        return productGateway.getProductById(id);
    }

}
