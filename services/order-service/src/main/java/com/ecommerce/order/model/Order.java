package com.ecommerce.order.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order Entity - Represents a customer order in the system
 *
 * JPA Annotations Explained:
 * @Entity - Marks this class as a JPA entity (database table)
 * @Table - Specifies the table name in database
 * @Id - Marks the primary key field
 * @GeneratedValue - Auto-generates the ID value
 * @Column - Configures column properties (nullable, unique, length)
 * @Enumerated - Maps Java enum to database column
 * @OneToMany - One order has many order items (relationship)
 * @CreationTimestamp - Auto-sets timestamp when entity is created
 * @UpdateTimestamp - Auto-updates timestamp when entity is modified
 */
@Entity
@Table(name = "orders")
public class Order {

    /**
     * Primary Key - Unique identifier for each order
     * UUID = Universally Unique Identifier (128-bit number)
     * Example: "550e8400-e29b-41d4-a716-446655440000"
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    /**
     * Order Number - Human-readable unique order reference
     * Example: "ORD-2024-001234"
     */
    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    /**
     * Customer Name
     */
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    /**
     * Customer Email
     */
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    /**
     * Total Amount - Uses BigDecimal for precise money calculations
     * precision = 10: total digits (including decimals)
     * scale = 2: digits after decimal point
     * Example: 12345.67 (total 7 digits, 2 after decimal)
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Order Status - Uses Enum for type safety
     * @Enumerated(EnumType.STRING) stores enum name as string in DB
     * Example: "PENDING", "CONFIRMED", "SHIPPED"
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    /**
     * Order Items - One-to-Many relationship
     * One order can have many items
     * mappedBy = "order": The "order" field in OrderItem class owns this relationship
     * cascade = CascadeType.ALL: When we save/delete Order, also save/delete its items
     * orphanRemoval = true: If an item is removed from this list, delete it from DB
     * fetch = FetchType.LAZY: Don't load items until we explicitly access them
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Created At - Automatically set when order is created
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Updated At - Automatically updated when order is modified
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Version - For optimistic locking (prevents concurrent update conflicts)
     * Automatically incremented by JPA on each update
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * No-args constructor - Required by JPA
     */
    public Order() {
    }

    /**
     * All-args constructor - Creates an Order with all fields
     */
    public Order(UUID orderId, String orderNumber, String customerName, String email,
                 BigDecimal totalAmount, OrderStatus status, List<OrderItem> orderItems,
                 LocalDateTime createdAt, LocalDateTime updatedAt, Long version) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.email = email;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = orderItems;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    /**
     * Getter for orderId
     */
    public UUID getOrderId() {
        return orderId;
    }

    /**
     * Setter for orderId
     */
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    /**
     * Getter for orderNumber
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Setter for orderNumber
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Getter for customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Setter for customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Getter for email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter for email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter for totalAmount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Setter for totalAmount
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Getter for status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Setter for status
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Getter for orderItems
     */
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    /**
     * Setter for orderItems
     */
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * Getter for createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Setter for createdAt
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Getter for updatedAt
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Setter for updatedAt
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Getter for version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Setter for version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Order Status Enum - Defines all possible order states
     */
    public enum OrderStatus {
        PENDING,      // Order created, awaiting payment
        CONFIRMED,    // Payment confirmed
        PROCESSING,   // Being prepared
        SHIPPED,      // Dispatched for delivery
        DELIVERED,    // Delivered to customer
        CANCELLED     // Order cancelled
    }
}
