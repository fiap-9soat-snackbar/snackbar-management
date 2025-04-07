package com.snackbar.productv2.infrastructure.gateways;

// This should be equivalent to the previous ProductRepository

import com.snackbar.productv2.application.gateways.Productv2Gateway;
import com.snackbar.productv2.domain.entity.Productv2;
import com.snackbar.productv2.infrastructure.persistence.Productv2Entity;
import com.snackbar.productv2.infrastructure.persistence.Productv2Repository;
import java.util.List;


public class Productv2RepositoryGateway implements Productv2Gateway {

    private final Productv2Repository productv2Repository;
    private final Productv2EntityMapper productv2EntityMapper;

    public Productv2RepositoryGateway(Productv2Repository productv2Repository, Productv2EntityMapper productv2EntityMapper) {
        this.productv2Repository = productv2Repository;
        this.productv2EntityMapper = productv2EntityMapper;
    }

    @Override
    public Productv2 createProductv2(Productv2 productv2DomainObj) {
        Productv2Entity productv2Entity = productv2EntityMapper.toEntity(productv2DomainObj);
        Productv2Entity savedObj = productv2Repository.save(productv2Entity);
        Productv2 createdProductv2 = productv2EntityMapper.toDomainObj(savedObj);
        return createdProductv2;
    }
    
    @Override
    public Productv2 getProductv2ById(String productv2Id) {
        Productv2Entity retrievedObj = productv2Repository.findById(productv2Id).orElse(null);
        Productv2 retrievedProductv2 = productv2EntityMapper.toDomainObj(retrievedObj);
        return retrievedProductv2;
    }

    @Override
    public List<Productv2> listProductsv2() {
        List<Productv2Entity> retrievedObjList = productv2Repository.findAll();
        List<Productv2> retrievedProductsv2List = productv2EntityMapper.toDomainListObj(retrievedObjList);
        return retrievedProductsv2List;
        
    }

    @Override
    public List<Productv2> getProductsv2ByCategory(String productv2Category) {
        List<Productv2Entity> retrievedObjList = productv2Repository.findByCategory(productv2Category);
        List<Productv2> retrievedProductsv2List = productv2EntityMapper.toDomainListObj(retrievedObjList);
        return retrievedProductsv2List;
    }

    @Override
    public Productv2 getProductv2ByName(String productv2Name) {
        Productv2Entity retrievedObj = productv2Repository.findByName(productv2Name).orElse(null);
        Productv2 retrievedProductv2 = productv2EntityMapper.toDomainObj(retrievedObj);
        return retrievedProductv2;
    }

    @Override
    public Productv2 updateProductv2ById(String id, Productv2 productv2) {
        Productv2Entity productv2Entity = productv2EntityMapper.toEntity(productv2);
        productv2Entity.setId(id);
        Productv2Entity savedObj = productv2Repository.save(productv2Entity);
        Productv2 updatedProductv2 = productv2EntityMapper.toDomainObj(savedObj);
        return updatedProductv2;
    }

    @Override
    public void deleteProductv2ById(String id) {
        Productv2Entity retrievedObj = productv2Repository.findById(id).orElse(null);
        productv2Repository.delete(retrievedObj);
    }

}
