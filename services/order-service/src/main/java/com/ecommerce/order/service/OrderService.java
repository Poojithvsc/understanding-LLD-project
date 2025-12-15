package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;

import java.util.List;
import java.util.UUID;

/**
 * OrderService - Business Logic Interface for Order operations
 *
 * What is a Service Layer?
 * - Contains business logic and rules
 * - Sits between Controller (handles HTTP) and Repository (handles database)
 * - Orchestrates operations, applies validations, transforms data
 *
 * Why use an interface?
 * 1. Abstraction: Defines WHAT operations exist, not HOW they work
 * 2. Testability: Easy to create mock implementations for testing
 * 3. Flexibility: Can have multiple implementations (e.g., with/without caching)
 * 4. Dependency Injection: Spring can inject any implementation
 *
 * Architecture Flow:
 * Client → Controller → Service (this interface) → Repository → Database
 */
public interface OrderService {

    /**
     * Create a new order
     *
     * Business Logic:
     * 1. Validate request data (Spring validation handles this)
     * 2. Generate unique order number (e.g., ORD-2024-001234)
     * 3. Calculate total amount from order items
     * 4. Set initial status to PENDING
     * 5. Save to database
     * 6. Return order response
     *
     * @param orderRequest Order data from client
     * @return OrderResponse with generated ID, order number, timestamps
     */
    OrderResponse createOrder(OrderRequest orderRequest);

    /**
     * Get order by ID
     *
     * @param orderId Order UUID
     * @return OrderResponse
     * @throws OrderNotFoundException if order not found (will create this exception later)
     */
    OrderResponse getOrderById(UUID orderId);

    /**
     * Get all orders
     *
     * Note: In production, this should use pagination
     * For learning purposes, we'll return all orders
     *
     * @return List of all orders
     */
    List<OrderResponse> getAllOrders();

    /**
     * Update an existing order
     *
     * Business Logic:
     * 1. Check if order exists
     * 2. Recalculate total amount if items changed
     * 3. Update fields
     * 4. Save to database
     *
     * @param orderId Order UUID
     * @param orderRequest Updated order data
     * @return Updated OrderResponse
     */
    OrderResponse updateOrder(UUID orderId, OrderRequest orderRequest);

    /**
     * Delete an order
     *
     * Business Logic:
     * 1. Check if order exists
     * 2. Check if order can be deleted (e.g., not if status is DELIVERED)
     * 3. Delete from database
     *
     * @param orderId Order UUID
     */
    void deleteOrder(UUID orderId);

    /**
     * Get order by order number
     *
     * @param orderNumber Order number (e.g., "ORD-2024-001234")
     * @return OrderResponse
     */
    OrderResponse getOrderByOrderNumber(String orderNumber);

    /**
     * Get orders by customer email
     *
     * @param email Customer email
     * @return List of orders for this customer
     */
    List<OrderResponse> getOrdersByEmail(String email);

    /**
     * Update order status
     *
     * Business Logic:
     * - Validate status transitions (e.g., can't go from DELIVERED to PENDING)
     *
     * @param orderId Order UUID
     * @param newStatus New order status
     * @return Updated OrderResponse
     */
    OrderResponse updateOrderStatus(UUID orderId, Order.OrderStatus newStatus);
}
