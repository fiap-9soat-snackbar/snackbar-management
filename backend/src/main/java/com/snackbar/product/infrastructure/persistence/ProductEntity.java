package com.snackbar.product.infrastructure.persistence;

// This should be the Product entity from the database perspective, not from the domain perspective.

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "products")
public class ProductEntity {

    @Id
    private String id;
    private String name;
    private String category;
    private String description;
    private BigDecimal price;
    private Integer cookingTime;

    public ProductEntity(String id, String name, String category, String description, BigDecimal price, Integer cookingTime) {
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

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }
}