package com.ecommerce.order.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderItem Entity - Represents individual items within an order
 *
 * Relationship: Many OrderItems belong to One Order
 * Example: Order #123 has 3 items (Laptop, Mouse, Keyboard)
 *
 * JPA Annotations Explained:
 * @ManyToOne - Many items can belong to one order
 * @JoinColumn - Specifies the foreign key column name
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    /**
     * Primary Key - Unique identifier for each order item
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_id", updatable = false, nullable = false)
    private UUID itemId;

    /**
     * Product ID - References the product in the catalog
     * In a real system, this would reference the Product Service
     */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * Product Name - Stored here for reference (snapshot at order time)
     * Why store name? Product details might change later, but order should
     * preserve what customer actually bought
     */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * Quantity - How many units of this product
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Price - Price per unit at the time of order
     * Stored to preserve historical pricing
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Order - Many-to-One relationship
     * Many OrderItems belong to One Order
     *
     * @ManyToOne - Defines the relationship type
     * @JoinColumn - Specifies foreign key column
     *   - name = "order_id": Column name in order_items table
     *   - nullable = false: Every item must belong to an order
     * fetch = FetchType.LAZY: Don't load the Order unless we explicitly access it
     *
     * Database Structure:
     * order_items table will have an "order_id" column that references orders.order_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * No-args constructor - Required by JPA
     */
    public OrderItem() {
    }

    /**
     * All-args constructor - Creates an OrderItem with all fields
     */
    public OrderItem(UUID itemId, UUID productId, String productName, Integer quantity,
                     BigDecimal price, Order order) {
        this.itemId = itemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.order = order;
    }

    /**
     * Getter for itemId
     */
    public UUID getItemId() {
        return itemId;
    }

    /**
     * Setter for itemId
     */
    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    /**
     * Getter for productId
     */
    public UUID getProductId() {
        return productId;
    }

    /**
     * Setter for productId
     */
    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    /**
     * Getter for productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Setter for productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Getter for quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Setter for quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Getter for price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Setter for price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Getter for order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Setter for order
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Helper method to calculate line total
     * Line Total = Quantity × Price
     * Example: 3 laptops × $999.99 = $2999.97
     */
    public BigDecimal getLineTotal() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
