package com.snackbar.product.infrastructure.messaging;

import java.time.ZoneOffset;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.snackbar.infrastructure.messaging.sqs.model.StandardProductMessage;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.DomainEvent;
import com.snackbar.product.domain.event.ProductCreatedEvent;
import com.snackbar.product.domain.event.ProductDeletedEvent;
import com.snackbar.product.domain.event.ProductUpdatedEvent;

/**
 * Maps between domain events and SQS messages.
 */
@Component
public class ProductMessageMapper {
    
    /**
     * Converts a domain event to an SQS message.
     * 
     * @param event The domain event to convert
     * @return The SQS message
     * @throws IllegalArgumentException if the event type is not supported
     */
    public StandardProductMessage toMessage(DomainEvent event) {
        StandardProductMessage message = new StandardProductMessage();
        
        // Set common fields
        message.setTimestamp(event.getOccurredOn().atZone(ZoneOffset.UTC).toInstant());
        
        // Set event-specific fields
        if (event instanceof ProductCreatedEvent createdEvent) {
            message.setEventType(StandardProductMessage.EVENT_TYPE_CREATED);
            mapProductToMessage(createdEvent.getProduct(), message);
        } else if (event instanceof ProductUpdatedEvent updatedEvent) {
            message.setEventType(StandardProductMessage.EVENT_TYPE_UPDATED);
            mapProductToMessage(updatedEvent.getProduct(), message);
        } else if (event instanceof ProductDeletedEvent deletedEvent) {
            message.setEventType(StandardProductMessage.EVENT_TYPE_DELETED);
            message.setProductId(deletedEvent.getProductId());
        } else {
            throw new IllegalArgumentException("Unsupported event type: " + event.getClass().getName());
        }
        
        return message;
    }
    
    /**
     * Maps a product entity to a message.
     * 
     * @param product The product entity
     * @param message The message to populate
     */
    private void mapProductToMessage(Product product, StandardProductMessage message) {
        message.setProductId(product.id());
        message.setName(product.name());
        message.setCategory(product.category());
        message.setDescription(product.description());
        message.setPrice(product.price());
        message.setCookingTime(product.cookingTime());
    }
    
    /**
     * Converts an SQS message to a product domain object.
     * 
     * @param message The SQS message
     * @return The product domain object
     */
    public Product toDomainObject(StandardProductMessage message) {
        String productId = message.getProductId();
        
        // If the ID is in UUID format and we're processing a create/update message,
        // set it to null to let MongoDB generate an ObjectId
        if ((message.getEventType().equals(StandardProductMessage.EVENT_TYPE_CREATED) || 
             message.getEventType().equals(StandardProductMessage.EVENT_TYPE_UPDATED)) && 
            productId != null && !isValidObjectId(productId)) {
            productId = null;
        }
        
        return new Product(
            productId,
            message.getName(),
            message.getCategory(),
            message.getDescription(),
            message.getPrice(),
            message.getCookingTime() != null ? message.getCookingTime() : 0
        );
    }
    
    // Helper method to check if a string is a valid ObjectId
    private boolean isValidObjectId(String id) {
        try {
            new ObjectId(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
