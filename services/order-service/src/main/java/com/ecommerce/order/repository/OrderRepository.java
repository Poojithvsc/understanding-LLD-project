package com.ecommerce.order.repository;

import com.ecommerce.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * OrderRepository - Data Access Layer for Order entity
 *
 * What is JpaRepository?
 * - It's a Spring Data JPA interface that provides CRUD operations automatically
 * - You don't need to write any implementation code!
 * - Spring generates the implementation at runtime
 *
 * Built-in Methods (inherited from JpaRepository):
 * - save(Order order) - Create or update an order
 * - findById(UUID id) - Find order by ID
 * - findAll() - Get all orders
 * - deleteById(UUID id) - Delete order by ID
 * - count() - Count total orders
 * - existsById(UUID id) - Check if order exists
 *
 * Generic Types:
 * - First type (Order): The entity type
 * - Second type (UUID): The primary key type
 *
 * @Repository annotation:
 * - Marks this as a Spring Data repository
 * - Enables exception translation (converts DB exceptions to Spring exceptions)
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Custom Query Method: Find order by order number
     *
     * Spring Data JPA Magic:
     * - Method name follows naming convention: findByOrderNumber
     * - Spring automatically generates SQL: SELECT * FROM orders WHERE order_number = ?
     * - No need to write @Query annotation for simple queries!
     *
     * @param orderNumber The order number to search for
     * @return Optional<Order> - Contains order if found, empty if not found
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Custom Query Method: Find orders by email
     *
     * Method naming convention:
     * - findBy + FieldName = SELECT * FROM orders WHERE field_name = ?
     *
     * @param email Customer email
     * @return List of all orders for this email
     */
    java.util.List<Order> findByEmail(String email);

    /**
     * Custom Query Method: Find orders by status
     *
     * @param status Order status (PENDING, CONFIRMED, etc.)
     * @return List of orders with this status
     */
    java.util.List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Custom Query Method: Check if order number already exists
     *
     * Spring generates: SELECT COUNT(*) > 0 FROM orders WHERE order_number = ?
     *
     * @param orderNumber Order number to check
     * @return true if exists, false if not
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Custom JPQL Query: Find orders by customer name (case-insensitive search)
     *
     * @Query annotation: Allows writing custom JPQL (Java Persistence Query Language)
     * JPQL vs SQL:
     * - JPQL: Works with Java entities (Order) instead of table names (orders)
     * - SQL: Works directly with database tables
     *
     * UPPER(o.customerName): Convert to uppercase for case-insensitive comparison
     * LIKE %:name%: Search for name anywhere in customerName
     *
     * @param name Customer name to search (partial match)
     * @return List of matching orders
     */
    @Query("SELECT o FROM Order o WHERE UPPER(o.customerName) LIKE UPPER(CONCAT('%', :name, '%'))")
    java.util.List<Order> findByCustomerNameContaining(@Param("name") String name);

    /**
     * Custom JPQL Query with JOIN: Find orders with their items loaded
     *
     * What is JOIN FETCH?
     * - Normal: Order is loaded, items are loaded separately (N+1 query problem)
     * - JOIN FETCH: Order and items loaded together in ONE query (better performance)
     *
     * This solves the "N+1 query problem":
     * - Without fetch: 1 query for order + N queries for items (if you have N orders)
     * - With fetch: 1 query for everything
     *
     * @param orderId Order ID
     * @return Optional<Order> with items eagerly loaded
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") UUID orderId);
}
