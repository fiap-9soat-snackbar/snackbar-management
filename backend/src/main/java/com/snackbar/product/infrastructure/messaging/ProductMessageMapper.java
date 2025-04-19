package com.snackbar.product.infrastructure.messaging;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.DomainEvent;
import com.snackbar.product.domain.event.ProductCreatedEvent;
import com.snackbar.product.domain.event.ProductDeletedEvent;
import com.snackbar.product.domain.event.ProductUpdatedEvent;

/**
 * Mapper to convert between domain objects/events and message models.
 * This class handles the translation between domain events and SQS messages.
 */
public class ProductMessageMapper {
    
    private static final String PRODUCT_CREATED_EVENT = "PRODUCT_CREATED";
    private static final String PRODUCT_UPDATED_EVENT = "PRODUCT_UPDATED";
    private static final String PRODUCT_DELETED_EVENT = "PRODUCT_DELETED";
    
    /**
     * Maps a domain event to a product message for SQS.
     *
     * @param event The domain event to map
     * @return The product message for SQS
     * @throws IllegalArgumentException if the event type is not supported
     */
    public ProductMessage toMessage(DomainEvent event) {
        if (event instanceof ProductCreatedEvent) {
            return mapProductCreatedEvent((ProductCreatedEvent) event);
        } else if (event instanceof ProductUpdatedEvent) {
            return mapProductUpdatedEvent((ProductUpdatedEvent) event);
        } else if (event instanceof ProductDeletedEvent) {
            return mapProductDeletedEvent((ProductDeletedEvent) event);
        } else {
            throw new IllegalArgumentException("Unsupported event type: " + event.getClass().getSimpleName());
        }
    }
    
    private ProductMessage mapProductCreatedEvent(ProductCreatedEvent event) {
        Product product = event.getProduct();
        ProductMessage.ProductData productData = createProductData(product);
        
        return new ProductMessage(
            UUID.randomUUID().toString(),
            PRODUCT_CREATED_EVENT,
            Instant.from(event.getOccurredOn().atZone(ZoneOffset.UTC)),
            productData
        );
    }
    
    private ProductMessage mapProductUpdatedEvent(ProductUpdatedEvent event) {
        Product product = event.getProduct();
        ProductMessage.ProductData productData = createProductData(product);
        
        return new ProductMessage(
            UUID.randomUUID().toString(),
            PRODUCT_UPDATED_EVENT,
            Instant.from(event.getOccurredOn().atZone(ZoneOffset.UTC)),
            productData
        );
    }
    
    private ProductMessage mapProductDeletedEvent(ProductDeletedEvent event) {
        ProductMessage.ProductData productData = new ProductMessage.ProductData();
        productData.setId(event.getProductId());
        
        return new ProductMessage(
            UUID.randomUUID().toString(),
            PRODUCT_DELETED_EVENT,
            Instant.from(event.getOccurredOn().atZone(ZoneOffset.UTC)),
            productData
        );
    }
    
    private ProductMessage.ProductData createProductData(Product product) {
        return new ProductMessage.ProductData(
            product.id(),
            product.name(),
            product.category(),
            product.description(),
            product.price(),
            product.cookingTime()
        );
    }
    
    /**
     * Maps a product message from SQS to a domain product.
     *
     * @param message The product message from SQS
     * @return The domain product
     */
    public Product toProduct(ProductMessage message) {
        ProductMessage.ProductData data = message.getProductData();
        return new Product(
            data.getId(),
            data.getName(),
            data.getCategory(),
            data.getDescription(),
            data.getPrice(),
            data.getCookingTime()
        );
    }
}
