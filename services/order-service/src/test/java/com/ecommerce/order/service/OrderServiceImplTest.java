package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderServiceImpl
 *
 * What we're testing:
 * - Business logic in the service layer
 * - Using Mockito to mock the repository (no real database calls)
 * - Verifying service behaves correctly with different inputs
 *
 * @ExtendWith(MockitoExtension.class) - Enables Mockito in JUnit 5
 * @Mock - Creates a mock instance of OrderRepository
 * @InjectMocks - Creates OrderServiceImpl and injects the mock repository into it
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl Unit Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderRequest testOrderRequest;
    private UUID testOrderId;
    private String testOrderNumber;

    /**
     * @BeforeEach - Runs before each test method
     * Sets up test data that multiple tests can use
     */
    @BeforeEach
    void setUp() {
        // Create test data
        testOrderId = UUID.randomUUID();
        testOrderNumber = "ORD-20251215-120000-TEST";

        // Create order items for testing
        OrderItem item1 = new OrderItem();
        item1.setItemId(UUID.randomUUID());
        item1.setProductId(UUID.randomUUID());
        item1.setProductName("Laptop");
        item1.setQuantity(1);
        item1.setPrice(new BigDecimal("999.99"));

        OrderItem item2 = new OrderItem();
        item2.setItemId(UUID.randomUUID());
        item2.setProductId(UUID.randomUUID());
        item2.setProductName("Mouse");
        item2.setQuantity(2);
        item2.setPrice(new BigDecimal("25.00"));

        // Create test order
        testOrder = new Order();
        testOrder.setOrderId(testOrderId);
        testOrder.setOrderNumber(testOrderNumber);
        testOrder.setCustomerName("John Doe");
        testOrder.setEmail("john@example.com");
        testOrder.setTotalAmount(new BigDecimal("1049.99"));
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());
        testOrder.setVersion(0L);

        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        item1.setOrder(testOrder);
        item2.setOrder(testOrder);
        testOrder.setOrderItems(items);

        // Create test order request
        OrderItemDto itemDto1 = new OrderItemDto();
        itemDto1.setProductId(UUID.randomUUID());
        itemDto1.setProductName("Laptop");
        itemDto1.setQuantity(1);
        itemDto1.setPrice(new BigDecimal("999.99"));

        OrderItemDto itemDto2 = new OrderItemDto();
        itemDto2.setProductId(UUID.randomUUID());
        itemDto2.setProductName("Mouse");
        itemDto2.setQuantity(2);
        itemDto2.setPrice(new BigDecimal("25.00"));

        testOrderRequest = new OrderRequest();
        testOrderRequest.setCustomerName("John Doe");
        testOrderRequest.setEmail("john@example.com");
        testOrderRequest.setOrderItems(List.of(itemDto1, itemDto2));
    }

    /**
     * Test 1: Create Order - Happy Path
     *
     * What we're testing:
     * - Service correctly creates an order from request
     * - Total amount is calculated correctly
     * - Repository save is called
     * - Response is mapped correctly
     */
    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder_Success() {
        // Arrange: Setup mock behavior
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderRepository.existsByOrderNumber(anyString())).thenReturn(false);

        // Act: Call the method we're testing
        OrderResponse response = orderService.createOrder(testOrderRequest);

        // Assert: Verify the results
        assertNotNull(response, "Response should not be null");
        assertEquals("John Doe", response.getCustomerName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(Order.OrderStatus.PENDING, response.getStatus());
        assertEquals(2, response.getOrderItems().size());

        // Verify: Check that repository.save() was called exactly once
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Test 2: Get Order By ID - Happy Path
     *
     * Tests that service correctly retrieves an order by ID
     */
    @Test
    @DisplayName("Should get order by ID successfully")
    void testGetOrderById_Success() {
        // Arrange: Mock repository to return test order
        when(orderRepository.findByIdWithItems(testOrderId)).thenReturn(Optional.of(testOrder));

        // Act
        OrderResponse response = orderService.getOrderById(testOrderId);

        // Assert
        assertNotNull(response);
        assertEquals(testOrderId, response.getOrderId());
        assertEquals(testOrderNumber, response.getOrderNumber());
        assertEquals("John Doe", response.getCustomerName());
        assertEquals("john@example.com", response.getEmail());

        // Verify
        verify(orderRepository, times(1)).findByIdWithItems(testOrderId);
    }

    /**
     * Test 3: Get Order By ID - Order Not Found
     *
     * Tests that service throws exception when order doesn't exist
     */
    @Test
    @DisplayName("Should throw exception when order not found by ID")
    void testGetOrderById_NotFound() {
        // Arrange: Mock repository to return empty Optional
        UUID nonExistentId = UUID.randomUUID();
        when(orderRepository.findByIdWithItems(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert: Verify that RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderRepository, times(1)).findByIdWithItems(nonExistentId);
    }

    /**
     * Test 4: Get All Orders
     *
     * Tests that service retrieves all orders from repository
     */
    @Test
    @DisplayName("Should get all orders successfully")
    void testGetAllOrders_Success() {
        // Arrange: Create a list of orders
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<OrderResponse> responses = orderService.getAllOrders();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testOrderNumber, responses.get(0).getOrderNumber());

        // Verify
        verify(orderRepository, times(1)).findAll();
    }

    /**
     * Test 5: Update Order - Happy Path
     *
     * Tests that service correctly updates an existing order
     */
    @Test
    @DisplayName("Should update order successfully")
    void testUpdateOrder_Success() {
        // Arrange
        when(orderRepository.findByIdWithItems(testOrderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Modify request data
        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerName("Jane Doe Updated");
        updateRequest.setEmail("jane.updated@example.com");
        updateRequest.setOrderItems(testOrderRequest.getOrderItems());

        // Act
        OrderResponse response = orderService.updateOrder(testOrderId, updateRequest);

        // Assert
        assertNotNull(response);
        verify(orderRepository, times(1)).findByIdWithItems(testOrderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Test 6: Update Order - Order Not Found
     *
     * Tests that update throws exception when order doesn't exist
     */
    @Test
    @DisplayName("Should throw exception when updating non-existent order")
    void testUpdateOrder_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(orderRepository.findByIdWithItems(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrder(nonExistentId, testOrderRequest);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * Test 7: Delete Order - Happy Path
     *
     * Tests that service deletes an order successfully
     */
    @Test
    @DisplayName("Should delete order successfully")
    void testDeleteOrder_Success() {
        // Arrange
        when(orderRepository.existsById(testOrderId)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(testOrderId);

        // Act
        orderService.deleteOrder(testOrderId);

        // Assert & Verify
        verify(orderRepository, times(1)).existsById(testOrderId);
        verify(orderRepository, times(1)).deleteById(testOrderId);
    }

    /**
     * Test 8: Delete Order - Order Not Found
     *
     * Tests that delete throws exception when order doesn't exist
     */
    @Test
    @DisplayName("Should throw exception when deleting non-existent order")
    void testDeleteOrder_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(orderRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.deleteOrder(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderRepository, never()).deleteById(any(UUID.class));
    }

    /**
     * Test 9: Get Order By Order Number - Happy Path
     *
     * Tests retrieving order by order number
     */
    @Test
    @DisplayName("Should get order by order number successfully")
    void testGetOrderByOrderNumber_Success() {
        // Arrange
        when(orderRepository.findByOrderNumber(testOrderNumber)).thenReturn(Optional.of(testOrder));

        // Act
        OrderResponse response = orderService.getOrderByOrderNumber(testOrderNumber);

        // Assert
        assertNotNull(response);
        assertEquals(testOrderNumber, response.getOrderNumber());
        assertEquals("John Doe", response.getCustomerName());

        // Verify
        verify(orderRepository, times(1)).findByOrderNumber(testOrderNumber);
    }

    /**
     * Test 10: Get Orders By Email
     *
     * Tests retrieving all orders for a specific customer email
     */
    @Test
    @DisplayName("Should get orders by email successfully")
    void testGetOrdersByEmail_Success() {
        // Arrange
        String email = "john@example.com";
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByEmail(email)).thenReturn(orders);

        // Act
        List<OrderResponse> responses = orderService.getOrdersByEmail(email);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(email, responses.get(0).getEmail());

        // Verify
        verify(orderRepository, times(1)).findByEmail(email);
    }

    /**
     * Test 11: Update Order Status - Happy Path
     *
     * Tests updating order status
     */
    @Test
    @DisplayName("Should update order status successfully")
    void testUpdateOrderStatus_Success() {
        // Arrange
        Order.OrderStatus newStatus = Order.OrderStatus.CONFIRMED;
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderResponse response = orderService.updateOrderStatus(testOrderId, newStatus);

        // Assert
        assertNotNull(response);
        verify(orderRepository, times(1)).findById(testOrderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Test 12: Update Order Status - Order Not Found
     *
     * Tests that status update throws exception when order doesn't exist
     */
    @Test
    @DisplayName("Should throw exception when updating status of non-existent order")
    void testUpdateOrderStatus_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        Order.OrderStatus newStatus = Order.OrderStatus.CONFIRMED;
        when(orderRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(nonExistentId, newStatus);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
