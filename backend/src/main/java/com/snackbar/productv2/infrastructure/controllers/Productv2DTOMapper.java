package com.snackbar.productv2.infrastructure.controllers;

import com.snackbar.productv2.domain.entity.Productv2;    
import java.util.List;

public class Productv2DTOMapper {
    CreateProductv2Response createToResponse(Productv2 productv2) {
        return new CreateProductv2Response(productv2.id(), productv2.name(), productv2.category(), productv2.description(), productv2.price(), productv2.cookingTime());
    }

    public Productv2 createRequestToDomain(CreateProductv2Request request) {
        return new Productv2(null, request.name(), request.category(), request.description(), request.price(), request.cookingTime());
    }

    GetProductv2Response getToResponse(Productv2 productv2) {
        return new GetProductv2Response(productv2.id(), productv2.name(), productv2.category(), productv2.description(), productv2.price(), productv2.cookingTime());
    }
    
    List<GetProductv2Response> listToResponse(List<Productv2> listProductsv2) {
        return listProductsv2.stream()
            .map(this::getToResponse)
            .toList();
    }
    
}
