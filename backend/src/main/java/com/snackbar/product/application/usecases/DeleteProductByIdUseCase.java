package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;

public class DeleteProductByIdUseCase {
    
    private final ProductGateway productGateway;

    public DeleteProductByIdUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public void deleteProductById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        productGateway.deleteProductById(id);
    }

}
