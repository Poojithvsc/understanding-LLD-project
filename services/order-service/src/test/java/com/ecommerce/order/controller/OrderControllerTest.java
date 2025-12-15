package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller Integration Tests for OrderController
 *
 * What we're testing:
 * - REST endpoints (HTTP requests and responses)
 * - Request mapping and routing
 * - Request/response JSON serialization
 * - HTTP status codes
 * - Using MockMvc to simulate HTTP requests
 *
 * @WebMvcTest - Loads only the web layer (controllers), not the full Spring context
 * @MockBean - Creates a mock of OrderService (business logic is not tested here)
 * MockMvc - Allows us to test controllers without starting a real server
 */
@WebMvcTest(OrderController.class)
@DisplayName("OrderController Integration Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // For converting objects to/from JSON

    @MockBean
    private OrderService orderService;

    private OrderRequest testOrderRequest;
    private OrderResponse testOrderResponse;
    private UUID testOrderId;
    private String testOrderNumber;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID();
        testOrderNumber = "ORD-20251215-120000-TEST";

        // Create test order items DTO
        OrderItemDto itemDto1 = new OrderItemDto();
        itemDto1.setItemId(UUID.randomUUID());
        itemDto1.setProductId(UUID.randomUUID());
        itemDto1.setProductName("Laptop");
        itemDto1.setQuantity(1);
        itemDto1.setPrice(new BigDecimal("999.99"));

        OrderItemDto itemDto2 = new OrderItemDto();
        itemDto2.setItemId(UUID.randomUUID());
        itemDto2.setProductId(UUID.randomUUID());
        itemDto2.setProductName("Mouse");
        itemDto2.setQuantity(2);
        itemDto2.setPrice(new BigDecimal("25.00"));

        // Create test order request
        testOrderRequest = new OrderRequest();
        testOrderRequest.setCustomerName("John Doe");
        testOrderRequest.setEmail("john@example.com");
        testOrderRequest.setOrderItems(List.of(itemDto1, itemDto2));

        // Create test order response
        testOrderResponse = new OrderResponse(
                testOrderId,
                testOrderNumber,
                "John Doe",
                "john@example.com",
                new BigDecimal("1049.99"),
                Order.OrderStatus.PENDING,
                List.of(itemDto1, itemDto2),
                LocalDateTime.now(),
                LocalDateTime.now(),
                0L
        );
    }

    /**
     * Test 1: POST /api/v1/orders - Create Order
     *
     * Tests that creating an order returns 201 CREATED with the order response
     */
    @Test
    @DisplayName("POST /api/v1/orders - Should create order and return 201 CREATED")
    void testCreateOrder_Success() throws Exception {
        // Arrange: Mock service to return test response
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(testOrderResponse);

        // Act & Assert: Send POST request and verify response
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isCreated())  // Expect HTTP 201
                .andExpect(jsonPath("$.orderId").value(testOrderId.toString()))
                .andExpect(jsonPath("$.orderNumber").value(testOrderNumber))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.totalAmount").value(1049.99))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.orderItems").isArray())
                .andExpect(jsonPath("$.orderItems", hasSize(2)));

        // Verify: Service was called once
        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    /**
     * Test 2: GET /api/v1/orders/{id} - Get Order By ID
     *
     * Tests retrieving an order by ID returns 200 OK with the order
     */
    @Test
    @DisplayName("GET /api/v1/orders/{id} - Should return order and 200 OK")
    void testGetOrderById_Success() throws Exception {
        // Arrange
        when(orderService.getOrderById(testOrderId)).thenReturn(testOrderResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/{id}", testOrderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Expect HTTP 200
                .andExpect(jsonPath("$.orderId").value(testOrderId.toString()))
                .andExpect(jsonPath("$.orderNumber").value(testOrderNumber))
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        // Verify
        verify(orderService, times(1)).getOrderById(testOrderId);
    }

    /**
     * Test 3: GET /api/v1/orders/{id} - Order Not Found
     *
     * Tests that requesting a non-existent order returns 500 (will improve with custom exception handler)
     */
    @Test
    @DisplayName("GET /api/v1/orders/{id} - Should return error when order not found")
    void testGetOrderById_NotFound() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(orderService.getOrderById(nonExistentId))
                .thenThrow(new RuntimeException("Order not found with ID: " + nonExistentId));

        // Act & Assert: The exception will propagate, so we expect it to be thrown
        // In a real application, we would have a @ControllerAdvice to handle this
        // For now, we just verify the service was called
        try {
            mockMvc.perform(get("/api/v1/orders/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is5xxServerError());  // Expect HTTP 500
        } catch (Exception e) {
            // Exception is expected - the service throws RuntimeException
            // and there's no @ControllerAdvice to handle it yet
            assertTrue(e.getCause() instanceof RuntimeException);
            assertTrue(e.getCause().getMessage().contains("Order not found"));
        }

        // Verify
        verify(orderService, times(1)).getOrderById(nonExistentId);
    }

    /**
     * Test 4: GET /api/v1/orders - Get All Orders
     *
     * Tests retrieving all orders returns 200 OK with an array
     */
    @Test
    @DisplayName("GET /api/v1/orders - Should return all orders and 200 OK")
    void testGetAllOrders_Success() throws Exception {
        // Arrange
        List<OrderResponse> orders = List.of(testOrderResponse);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderNumber").value(testOrderNumber));

        // Verify
        verify(orderService, times(1)).getAllOrders();
    }

    /**
     * Test 5: PUT /api/v1/orders/{id} - Update Order
     *
     * Tests updating an order returns 200 OK with updated data
     */
    @Test
    @DisplayName("PUT /api/v1/orders/{id} - Should update order and return 200 OK")
    void testUpdateOrder_Success() throws Exception {
        // Arrange
        OrderResponse updatedResponse = new OrderResponse(
                testOrderId,
                testOrderNumber,
                "Jane Doe Updated",
                "jane.updated@example.com",
                new BigDecimal("1049.99"),
                Order.OrderStatus.PENDING,
                testOrderResponse.getOrderItems(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );
        when(orderService.updateOrder(eq(testOrderId), any(OrderRequest.class))).thenReturn(updatedResponse);

        // Modify request
        testOrderRequest.setCustomerName("Jane Doe Updated");
        testOrderRequest.setEmail("jane.updated@example.com");

        // Act & Assert
        mockMvc.perform(put("/api/v1/orders/{id}", testOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Jane Doe Updated"))
                .andExpect(jsonPath("$.email").value("jane.updated@example.com"));

        // Verify
        verify(orderService, times(1)).updateOrder(eq(testOrderId), any(OrderRequest.class));
    }

    /**
     * Test 6: DELETE /api/v1/orders/{id} - Delete Order
     *
     * Tests deleting an order returns 204 NO CONTENT
     */
    @Test
    @DisplayName("DELETE /api/v1/orders/{id} - Should delete order and return 204 NO CONTENT")
    void testDeleteOrder_Success() throws Exception {
        // Arrange
        doNothing().when(orderService).deleteOrder(testOrderId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/orders/{id}", testOrderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());  // Expect HTTP 204

        // Verify
        verify(orderService, times(1)).deleteOrder(testOrderId);
    }

    /**
     * Test 7: GET /api/v1/orders/number/{orderNumber} - Get Order By Order Number
     *
     * Tests retrieving order by order number
     */
    @Test
    @DisplayName("GET /api/v1/orders/number/{orderNumber} - Should return order and 200 OK")
    void testGetOrderByOrderNumber_Success() throws Exception {
        // Arrange
        when(orderService.getOrderByOrderNumber(testOrderNumber)).thenReturn(testOrderResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/number/{orderNumber}", testOrderNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value(testOrderNumber))
                .andExpect(jsonPath("$.customerName").value("John Doe"));

        // Verify
        verify(orderService, times(1)).getOrderByOrderNumber(testOrderNumber);
    }

    /**
     * Test 8: GET /api/v1/orders/customer?email={email} - Get Orders By Email
     *
     * Tests retrieving orders by customer email using query parameter
     */
    @Test
    @DisplayName("GET /api/v1/orders/customer?email={email} - Should return orders and 200 OK")
    void testGetOrdersByEmail_Success() throws Exception {
        // Arrange
        String email = "john@example.com";
        List<OrderResponse> orders = List.of(testOrderResponse);
        when(orderService.getOrdersByEmail(email)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/customer")
                        .param("email", email)  // Query parameter
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value(email));

        // Verify
        verify(orderService, times(1)).getOrdersByEmail(email);
    }

    /**
     * Test 9: PATCH /api/v1/orders/{id}/status - Update Order Status
     *
     * Tests updating order status using PATCH request
     */
    @Test
    @DisplayName("PATCH /api/v1/orders/{id}/status - Should update status and return 200 OK")
    void testUpdateOrderStatus_Success() throws Exception {
        // Arrange
        Order.OrderStatus newStatus = Order.OrderStatus.CONFIRMED;
        OrderResponse confirmedResponse = new OrderResponse(
                testOrderId,
                testOrderNumber,
                "John Doe",
                "john@example.com",
                new BigDecimal("1049.99"),
                Order.OrderStatus.CONFIRMED,  // Updated status
                testOrderResponse.getOrderItems(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L
        );
        when(orderService.updateOrderStatus(testOrderId, newStatus)).thenReturn(confirmedResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/orders/{id}/status", testOrderId)
                        .param("status", "CONFIRMED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // Verify
        verify(orderService, times(1)).updateOrderStatus(testOrderId, newStatus);
    }

    /**
     * Test 10: POST /api/v1/orders - Validation Failure (Invalid Email)
     *
     * Tests that invalid request data returns 400 BAD REQUEST
     */
    @Test
    @DisplayName("POST /api/v1/orders - Should return 400 BAD REQUEST for invalid data")
    void testCreateOrder_ValidationFailure() throws Exception {
        // Arrange: Create invalid request (invalid email)
        testOrderRequest.setEmail("invalid-email");  // Not a valid email format

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isBadRequest());  // Expect HTTP 400

        // Verify: Service should NOT be called due to validation failure
        verify(orderService, never()).createOrder(any(OrderRequest.class));
    }

    /**
     * Test 11: GET /api/v1/orders - Empty List
     *
     * Tests that getting all orders returns empty array when no orders exist
     */
    @Test
    @DisplayName("GET /api/v1/orders - Should return empty array when no orders exist")
    void testGetAllOrders_EmptyList() throws Exception {
        // Arrange
        when(orderService.getAllOrders()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify
        verify(orderService, times(1)).getAllOrders();
    }

    /**
     * Test 12: GET /api/v1/orders/customer?email={email} - Empty List
     *
     * Tests that getting orders by email returns empty array when customer has no orders
     */
    @Test
    @DisplayName("GET /api/v1/orders/customer?email={email} - Should return empty array")
    void testGetOrdersByEmail_EmptyList() throws Exception {
        // Arrange
        String email = "noorders@example.com";
        when(orderService.getOrdersByEmail(email)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/customer")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify
        verify(orderService, times(1)).getOrdersByEmail(email);
    }
}
