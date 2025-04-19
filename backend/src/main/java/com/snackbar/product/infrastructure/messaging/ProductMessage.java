package com.snackbar.product.infrastructure.messaging;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Message model for SQS communication related to products.
 * This class represents the structure of messages sent to/from SQS.
 */
public class ProductMessage {
    
    private String messageId;
    private String eventType;
    private Instant timestamp;
    private ProductData productData;
    
    // Default constructor for serialization/deserialization
    public ProductMessage() {
    }
    
    public ProductMessage(String messageId, String eventType, Instant timestamp, ProductData productData) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.productData = productData;
    }
    
    // Nested class for product data
    public static class ProductData {
        private String id;
        private String name;
        private String category;
        private String description;
        private BigDecimal price;
        private int cookingTime;
        
        // Default constructor for serialization/deserialization
        public ProductData() {
        }
        
        public ProductData(String id, String name, String category, String description, BigDecimal price, int cookingTime) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.description = description;
            this.price = price;
            this.cookingTime = cookingTime;
        }
        
        // Getters and setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
        
        public int getCookingTime() {
            return cookingTime;
        }
        
        public void setCookingTime(int cookingTime) {
            this.cookingTime = cookingTime;
        }
    }
    
    // Getters and setters
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public ProductData getProductData() {
        return productData;
    }
    
    public void setProductData(ProductData productData) {
        this.productData = productData;
    }
}
