package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;

public class DeleteProductByIdUseCase {
    
    private final ProductGateway productGateway;

    public DeleteProductByIdUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public void deleteProductById(String id) {
        productGateway.deleteProductById(id);
    }
        
}
