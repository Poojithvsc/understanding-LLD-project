package com.ecommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * OrderRequest - DTO for creating/updating an order
 */
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Customer name must be between 2 and 100 characters")
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotNull(message = "Order items are required")
    @NotEmpty(message = "Order must have at least one item")
    @Size(max = 50, message = "Order cannot have more than 50 items")
    @Valid
    private List<OrderItemDto> orderItems;

    // Constructors
    public OrderRequest() {}

    public OrderRequest(String customerName, String email, List<OrderItemDto> orderItems) {
        this.customerName = customerName;
        this.email = email;
        this.orderItems = orderItems;
    }

    // Getters and Setters
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }
}
