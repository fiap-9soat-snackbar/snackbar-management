package com.snackbar.productv2.application.usecases;

import com.snackbar.productv2.application.gateways.Productv2Gateway;
import com.snackbar.productv2.domain.entity.Productv2;

public class GetProductv2ByNameUseCase {
        
    private final Productv2Gateway productv2Gateway;

    public GetProductv2ByNameUseCase(Productv2Gateway productv2Gateway) {
        this.productv2Gateway = productv2Gateway;
    }

    public Productv2 getProductv2ByName(String name) {
        Productv2 retrievedProductv2 = productv2Gateway.getProductv2ByName(name);
        return retrievedProductv2;
    }

}