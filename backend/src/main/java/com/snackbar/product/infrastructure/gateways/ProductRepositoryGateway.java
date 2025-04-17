package com.snackbar.product.infrastructure.gateways;

import java.util.List;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.persistence.ProductEntity;
import com.snackbar.product.infrastructure.persistence.ProductRepository;


public class ProductRepositoryGateway implements ProductGateway {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    public ProductRepositoryGateway(ProductRepository productRepository, ProductEntityMapper productEntityMapper) {
        this.productRepository = productRepository;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public Product createProduct(Product productDomainObj) {
        ProductEntity productEntity = productEntityMapper.toEntity(productDomainObj);
        ProductEntity savedObj = productRepository.save(productEntity);
        Product createdProduct = productEntityMapper.toDomainObj(savedObj);
        return createdProduct;
    }
    
    @Override
    public Product getProductById(String productId) {
        ProductEntity retrievedObj = productRepository.findById(productId).orElse(null);
        Product retrievedProduct = productEntityMapper.toDomainObj(retrievedObj);
        return retrievedProduct;
    }

    @Override
    public List<Product> listProduct() {
        List<ProductEntity> retrievedObjList = productRepository.findAll();
        List<Product> retrievedProductList = productEntityMapper.toDomainListObj(retrievedObjList);
        return retrievedProductList;
        
    }

    @Override
    public List<Product> getProductByCategory(String productCategory) {
        List<ProductEntity> retrievedObjList = productRepository.findByCategory(productCategory);
        List<Product> retrievedProductsList = productEntityMapper.toDomainListObj(retrievedObjList);
        return retrievedProductsList;
    }

    @Override
    public Product getProductByName(String productName) {
        ProductEntity retrievedObj = productRepository.findByName(productName).orElse(null);
        Product retrievedProduct = productEntityMapper.toDomainObj(retrievedObj);
        return retrievedProduct;
    }

    @Override
    public Product updateProductById(String id, Product product) {
        ProductEntity productEntity = productEntityMapper.toEntity(product);
        productEntity.setId(id);
        ProductEntity savedObj = productRepository.save(productEntity);
        Product updatedProduct = productEntityMapper.toDomainObj(savedObj);
        return updatedProduct;
    }

    @Override
    public void deleteProductById(String id) {
        ProductEntity retrievedObj = productRepository.findById(id).orElse(null);
        productRepository.delete(retrievedObj);
    }

}
