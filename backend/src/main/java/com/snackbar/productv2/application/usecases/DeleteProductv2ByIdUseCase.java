package com.snackbar.productv2.application.usecases;

import com.snackbar.productv2.application.gateways.Productv2Gateway;

public class DeleteProductv2ByIdUseCase {
    
    private final Productv2Gateway productv2Gateway;

    public DeleteProductv2ByIdUseCase(Productv2Gateway productv2Gateway) {
        this.productv2Gateway = productv2Gateway;
    }

    public void deleteProductv2ById(String id) {
        productv2Gateway.deleteProductv2ById(id);
    }
        
}
