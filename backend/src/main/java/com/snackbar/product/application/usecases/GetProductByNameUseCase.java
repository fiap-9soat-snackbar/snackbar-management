package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;

public class GetProductByNameUseCase {
    
    private final ProductGateway productGateway;

    public GetProductByNameUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Product getProductByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        return productGateway.getProductByName(name);
    }

}
