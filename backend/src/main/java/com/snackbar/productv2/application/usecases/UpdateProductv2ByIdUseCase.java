package com.snackbar.productv2.application.usecases;

// This should be equivalent to a Spring Service definition, 
// but without any framework dependencies. It's also called an Interactor.

import com.snackbar.productv2.application.gateways.Productv2Gateway;
import com.snackbar.productv2.domain.entity.Productv2;

public class UpdateProductv2ByIdUseCase {

    private final Productv2Gateway productv2Gateway;
    private final GetProductv2ByIdUseCase getProductv2ByIdUseCase;

    public UpdateProductv2ByIdUseCase(Productv2Gateway productv2Gateway,
                                      GetProductv2ByIdUseCase getProductv2ByIdUseCase) {
        this.productv2Gateway = productv2Gateway;
        this.getProductv2ByIdUseCase = getProductv2ByIdUseCase;
    }

    public Productv2 updateProductv2ById(String id, Productv2 productv2) {
        Productv2 locatedProductv2 = getProductv2ByIdUseCase.getProductv2ById(id);
        Productv2 updatedProductv2 = productv2Gateway.updateProductv2ById(locatedProductv2.id(), productv2);
        return updatedProductv2;
    }

}