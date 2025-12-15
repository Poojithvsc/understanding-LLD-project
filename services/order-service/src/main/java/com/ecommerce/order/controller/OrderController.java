package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * OrderController - REST API Controller for Order operations
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    // Constructor for dependency injection
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * CREATE: Create a new order
     *
     * Endpoint: POST /api/v1/orders
     * Request Body: OrderRequest JSON
     * Response: 201 CREATED with OrderResponse
     *
     * @Valid annotation: Triggers validation on OrderRequest
     * - Checks @NotBlank, @Email, @Size, etc.
     * - If validation fails, returns 400 BAD REQUEST automatically
     *
     * Example Request:
     * POST http://localhost:8080/api/v1/orders
     * {
     *   "customerName": "John Doe",
     *   "email": "john@example.com",
     *   "orderItems": [
     *     {
     *       "productId": "550e8400-e29b-41d4-a716-446655440000",
     *       "productName": "Laptop",
     *       "quantity": 1,
     *       "price": 999.99
     *     }
     *   ]
     * }
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        log.info("POST /api/v1/orders - Creating order for: {}", orderRequest.getCustomerName());

        OrderResponse response = orderService.createOrder(orderRequest);

        log.info("Order created successfully: {}", response.getOrderNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        // Returns HTTP 201 CREATED with the order details
    }

    /**
     * READ: Get order by ID
     *
     * Endpoint: GET /api/v1/orders/{id}
     * Path Variable: id (UUID)
     * Response: 200 OK with OrderResponse
     *
     * @PathVariable: Extracts value from URL path
     *
     * Example Request:
     * GET http://localhost:8080/api/v1/orders/550e8400-e29b-41d4-a716-446655440000
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("id") UUID orderId) {
        log.info("GET /api/v1/orders/{} - Fetching order", orderId);

        OrderResponse response = orderService.getOrderById(orderId);

        return ResponseEntity.ok(response);
        // Returns HTTP 200 OK with the order
    }

    /**
     * READ: Get all orders
     *
     * Endpoint: GET /api/v1/orders
     * Response: 200 OK with List<OrderResponse>
     *
     * Example Request:
     * GET http://localhost:8080/api/v1/orders
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("GET /api/v1/orders - Fetching all orders");

        List<OrderResponse> orders = orderService.getAllOrders();

        log.info("Found {} orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    /**
     * UPDATE: Update an existing order
     *
     * Endpoint: PUT /api/v1/orders/{id}
     * Path Variable: id (UUID)
     * Request Body: OrderRequest JSON
     * Response: 200 OK with updated OrderResponse
     *
     * Example Request:
     * PUT http://localhost:8080/api/v1/orders/550e8400-e29b-41d4-a716-446655440000
     * {
     *   "customerName": "John Doe Updated",
     *   "email": "john.updated@example.com",
     *   "orderItems": [...]
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable("id") UUID orderId,
            @Valid @RequestBody OrderRequest orderRequest) {

        log.info("PUT /api/v1/orders/{} - Updating order", orderId);

        OrderResponse response = orderService.updateOrder(orderId, orderRequest);

        log.info("Order updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE: Delete an order
     *
     * Endpoint: DELETE /api/v1/orders/{id}
     * Path Variable: id (UUID)
     * Response: 204 NO CONTENT
     *
     * Example Request:
     * DELETE http://localhost:8080/api/v1/orders/550e8400-e29b-41d4-a716-446655440000
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") UUID orderId) {
        log.info("DELETE /api/v1/orders/{} - Deleting order", orderId);

        orderService.deleteOrder(orderId);

        log.info("Order deleted successfully");
        return ResponseEntity.noContent().build();
        // Returns HTTP 204 NO CONTENT (success, no body)
    }

    /**
     * READ: Get order by order number
     *
     * Endpoint: GET /api/v1/orders/number/{orderNumber}
     * Path Variable: orderNumber (String)
     * Response: 200 OK with OrderResponse
     *
     * Example Request:
     * GET http://localhost:8080/api/v1/orders/number/ORD-20241214-143052-A4B9
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        log.info("GET /api/v1/orders/number/{} - Fetching order", orderNumber);

        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);

        return ResponseEntity.ok(response);
    }

    /**
     * READ: Get orders by customer email
     *
     * Endpoint: GET /api/v1/orders/customer?email={email}
     * Query Parameter: email
     * Response: 200 OK with List<OrderResponse>
     *
     * @RequestParam: Extracts query parameter from URL
     *
     * Example Request:
     * GET http://localhost:8080/api/v1/orders/customer?email=john@example.com
     */
    @GetMapping("/customer")
    public ResponseEntity<List<OrderResponse>> getOrdersByEmail(@RequestParam String email) {
        log.info("GET /api/v1/orders/customer?email={} - Fetching orders", email);

        List<OrderResponse> orders = orderService.getOrdersByEmail(email);

        log.info("Found {} orders for email: {}", orders.size(), email);
        return ResponseEntity.ok(orders);
    }

    /**
     * PATCH: Update order status
     *
     * Endpoint: PATCH /api/v1/orders/{id}/status
     * Path Variable: id (UUID)
     * Query Parameter: status (OrderStatus enum)
     * Response: 200 OK with updated OrderResponse
     *
     * PATCH vs PUT:
     * - PUT: Replace entire resource
     * - PATCH: Update specific fields only
     *
     * Example Request:
     * PATCH http://localhost:8080/api/v1/orders/550e8400-e29b-41d4-a716-446655440000/status?status=CONFIRMED
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable("id") UUID orderId,
            @RequestParam Order.OrderStatus status) {

        log.info("PATCH /api/v1/orders/{}/status - Updating status to {}", orderId, status);

        OrderResponse response = orderService.updateOrderStatus(orderId, status);

        log.info("Order status updated successfully");
        return ResponseEntity.ok(response);
    }
}
