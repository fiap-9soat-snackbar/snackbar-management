package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;

public class UpdateProductByIdUseCase {

    private final ProductGateway productGateway;
    private final GetProductByIdUseCase getProductByIdUseCase;

    public UpdateProductByIdUseCase(ProductGateway productGateway,
                                      GetProductByIdUseCase getProductByIdUseCase) {
        this.productGateway = productGateway;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    public Product updateProductById(String id, Product product) {
        Product locatedProduct = getProductByIdUseCase.getProductById(id);
        Product updatedProduct = productGateway.updateProductById(locatedProduct.id(), product);
        return updatedProduct;
    }

}