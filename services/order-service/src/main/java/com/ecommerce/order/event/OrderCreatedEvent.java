package com.ecommerce.order.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Event published when an order is created
 * This event will be sent to Kafka and consumed by Inventory Service
 */
public class OrderCreatedEvent implements Serializable {

    private UUID orderId;
    private String orderNumber;
    private String customerName;
    private String email;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItemEvent> orderItems;

    // Constructors
    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(UUID orderId, String orderNumber, String customerName, String email,
                             BigDecimal totalAmount, String status, List<OrderItemEvent> orderItems) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.email = email;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = orderItems;
    }

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemEvent> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemEvent> orderItems) {
        this.orderItems = orderItems;
    }

    // Nested class for order items
    public static class OrderItemEvent implements Serializable {
        private UUID productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;

        public OrderItemEvent() {
        }

        public OrderItemEvent(UUID productId, String productName, Integer quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
