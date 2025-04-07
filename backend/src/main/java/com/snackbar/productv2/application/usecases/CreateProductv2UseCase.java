package com.snackbar.productv2.application.usecases;

// This should be equivalent to a Spring Service definition, 
// but without any framework dependencies. It's also called an Interactor.

import com.snackbar.productv2.application.gateways.Productv2Gateway;
import com.snackbar.productv2.domain.entity.Productv2;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/

public class CreateProductv2UseCase {
    
    //private static final Logger logger = LoggerFactory.getLogger(CreateProductUseCase.class);

    private final Productv2Gateway productv2Gateway;

    public CreateProductv2UseCase(Productv2Gateway productv2Gateway) {
        this.productv2Gateway = productv2Gateway;
    }

    public Productv2 createProductv2(Productv2 productv2) {
        //if (logger != null) logger.info("Starting product creation process");
        Productv2 createdProductv2 = productv2Gateway.createProductv2(productv2);
        //if (logger != null) logger.info("Product creation completed with id:");
        return createdProductv2;
    }

}
