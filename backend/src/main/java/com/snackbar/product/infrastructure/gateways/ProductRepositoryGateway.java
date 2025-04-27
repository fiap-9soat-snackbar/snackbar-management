package com.snackbar.product.infrastructure.gateways;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import com.snackbar.product.infrastructure.persistence.ProductEntity;
import com.snackbar.product.infrastructure.persistence.ProductRepository;

public class ProductRepositoryGateway implements ProductGateway {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryGateway.class);

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    public ProductRepositoryGateway(ProductRepository productRepository, ProductEntityMapper productEntityMapper) {
        this.productRepository = productRepository;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public Product createProduct(Product productDomainObj) {
        try {
            logger.debug("Converting domain object to entity: {}", productDomainObj);
            // Convert domain object to entity - the mapper will handle ID standardization
            ProductEntity productEntity = productEntityMapper.toEntity(productDomainObj);
            logger.debug("Converted to entity: {}", productEntity);
            
            // Let MongoDB generate an ObjectId if id is null
            logger.debug("Saving product entity to MongoDB");
            ProductEntity savedObj = productRepository.save(productEntity);
            logger.debug("Saved entity: {}", savedObj);
            
            Product createdProduct = productEntityMapper.toDomainObj(savedObj);
            logger.debug("Converted saved entity back to domain object: {}", createdProduct);
            return createdProduct;
        } catch (Exception e) {
            logger.error("Error creating product in repository", e);
            throw e;
        }
    }
    
    @Override
    public Product getProductById(String productId) {
        ProductEntity retrievedObj = productRepository.findById(productId)
            .orElseThrow(() -> ProductNotFoundException.withId(productId));
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
        ProductEntity retrievedObj = productRepository.findByName(productName)
            .orElseThrow(() -> ProductNotFoundException.withName(productName));
        Product retrievedProduct = productEntityMapper.toDomainObj(retrievedObj);
        return retrievedProduct;
    }

    @Override
    public Product updateProductById(String id, Product product) {
        // Check if product exists
        if (!productRepository.existsById(id)) {
            throw ProductNotFoundException.withId(id);
        }
        
        ProductEntity productEntity = productEntityMapper.toEntity(product);
        productEntity.setId(id);
        ProductEntity savedObj = productRepository.save(productEntity);
        Product updatedProduct = productEntityMapper.toDomainObj(savedObj);
        return updatedProduct;
    }

    @Override
    public void deleteProductById(String id) {
        // Modified to match the test expectations
        Optional<ProductEntity> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            productRepository.delete(productOpt.get());
        } else {
            throw ProductNotFoundException.withId(id);
        }
    }
}
