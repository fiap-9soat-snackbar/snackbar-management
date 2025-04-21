package com.snackbar.infrastructure.messaging.sqs.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Standardized message format for product events.
 * This class represents the unified format for all product messages.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandardProductMessage extends SQSMessage {
    
    public static final String EVENT_TYPE_CREATED = "PRODUCT_CREATED";
    public static final String EVENT_TYPE_UPDATED = "PRODUCT_UPDATED";
    public static final String EVENT_TYPE_DELETED = "PRODUCT_DELETED";
    
    private String productId;
    private String name;
    private String category;
    private String description;
    private BigDecimal price;
    private Integer cookingTime;
    
    /**
     * Default constructor for deserialization.
     */
    public StandardProductMessage() {
        super();
    }
    
    /**
     * Constructor with event type.
     * 
     * @param eventType The event type
     */
    public StandardProductMessage(String eventType) {
        super(eventType);
    }
    
    /**
     * Constructor with all fields.
     * 
     * @param messageId The message ID
     * @param eventType The event type
     * @param timestamp The timestamp
     * @param productId The product ID
     * @param name The product name
     * @param category The product category
     * @param description The product description
     * @param price The product price
     * @param cookingTime The product cooking time
     */
    public StandardProductMessage(String messageId, String eventType, Instant timestamp, 
            String productId, String name, String category, String description, 
            BigDecimal price, Integer cookingTime) {
        super(messageId, eventType, timestamp);
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.cookingTime = cookingTime;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
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
    
    public Integer getCookingTime() {
        return cookingTime;
    }
    
    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }
}
