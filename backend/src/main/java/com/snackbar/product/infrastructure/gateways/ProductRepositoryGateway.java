package com.snackbar.product.infrastructure.gateways;

import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import com.snackbar.product.infrastructure.persistence.ProductEntity;
import com.snackbar.product.infrastructure.persistence.ProductRepository;

public class ProductRepositoryGateway implements ProductGateway {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryGateway.class);

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;
    private final MongoTemplate mongoTemplate;

    public ProductRepositoryGateway(ProductRepository productRepository, ProductEntityMapper productEntityMapper, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.productEntityMapper = productEntityMapper;
        this.mongoTemplate = mongoTemplate;
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
        try {
            // First try to find by string ID
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                return;
            }
            
            // If not found and the ID looks like an ObjectId, try to convert and find
            if (id.matches("[0-9a-f]{24}")) {
                // Use a custom query to find by ObjectId
                Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
                ProductEntity product = mongoTemplate.findOne(query, ProductEntity.class, "products");
                if (product != null) {
                    mongoTemplate.remove(product, "products");
                    return;
                }
            }
            
            // If we get here, the product wasn't found
            throw ProductNotFoundException.withId(id);
        } catch (IllegalArgumentException e) {
            // This happens if the ID format is invalid
            throw ProductNotFoundException.withId(id);
        }
    }
}
