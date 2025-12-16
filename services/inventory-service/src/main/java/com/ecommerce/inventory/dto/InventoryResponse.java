package com.ecommerce.inventory.dto;

import com.ecommerce.inventory.model.Inventory;

import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryResponse {

    private UUID id;
    private ProductResponse product;
    private Integer quantity;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public InventoryResponse() {
    }

    public InventoryResponse(Inventory inventory) {
        this.id = inventory.getId();
        this.product = new ProductResponse(inventory.getProduct());
        this.quantity = inventory.getQuantity();
        this.location = inventory.getLocation();
        this.createdAt = inventory.getCreatedAt();
        this.updatedAt = inventory.getUpdatedAt();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public void setProduct(ProductResponse product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
