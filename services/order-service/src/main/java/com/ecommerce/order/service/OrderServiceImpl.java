package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemDto;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OrderServiceImpl - Implementation of OrderService interface
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;

    // Constructor for dependency injection
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Create a new order
     */
    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating new order for customer: {}", orderRequest.getCustomerName());

        // 1. Create Order entity
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());  // Generate unique order number
        order.setCustomerName(orderRequest.getCustomerName());
        order.setEmail(orderRequest.getEmail());
        order.setStatus(Order.OrderStatus.PENDING);  // Initial status

        // 2. Create OrderItems and calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                .map(itemDto -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(itemDto.getProductId());
                    item.setProductName(itemDto.getProductName());
                    item.setQuantity(itemDto.getQuantity());
                    item.setPrice(itemDto.getPrice());
                    item.setOrder(order);  // Set the relationship
                    return item;
                })
                .collect(Collectors.toList());

        // Calculate total amount
        for (OrderItem item : orderItems) {
            totalAmount = totalAmount.add(item.getLineTotal());
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        // 3. Save to database
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {} and order number: {}",
                savedOrder.getOrderId(), savedOrder.getOrderNumber());

        // 4. Convert to response DTO and return
        return mapToResponse(savedOrder);
    }

    /**
     * Get order by ID
     */
    @Override
    @Transactional(readOnly = true)  // Read-only transaction (performance optimization)
    public OrderResponse getOrderById(UUID orderId) {
        log.info("Fetching order with ID: {}", orderId);

        // findByIdWithItems uses JOIN FETCH to load items in one query
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        // TODO: Replace RuntimeException with custom OrderNotFoundException in Part 4

        return mapToResponse(order);
    }

    /**
     * Get all orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");

        List<Order> orders = orderRepository.findAll();
        log.info("Found {} orders", orders.size());

        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing order
     */
    @Override
    public OrderResponse updateOrder(UUID orderId, OrderRequest orderRequest) {
        log.info("Updating order with ID: {}", orderId);

        // 1. Find existing order
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // 2. Update basic fields
        order.setCustomerName(orderRequest.getCustomerName());
        order.setEmail(orderRequest.getEmail());

        // 3. Clear existing items and add new ones
        order.getOrderItems().clear();

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDto itemDto : orderRequest.getOrderItems()) {
            OrderItem item = new OrderItem();
            item.setProductId(itemDto.getProductId());
            item.setProductName(itemDto.getProductName());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(itemDto.getPrice());
            item.setOrder(order);
            order.getOrderItems().add(item);

            totalAmount = totalAmount.add(item.getLineTotal());
        }

        order.setTotalAmount(totalAmount);

        // 4. Save updated order
        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated successfully: {}", orderId);

        return mapToResponse(updatedOrder);
    }

    /**
     * Delete an order
     */
    @Override
    public void deleteOrder(UUID orderId) {
        log.info("Deleting order with ID: {}", orderId);

        // Check if order exists
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        // TODO: Add business logic to prevent deletion of completed orders
        // if (order.getStatus() == OrderStatus.DELIVERED) {
        //     throw new IllegalStateException("Cannot delete delivered orders");
        // }

        orderRepository.deleteById(orderId);
        log.info("Order deleted successfully: {}", orderId);
    }

    /**
     * Get order by order number
     */
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        log.info("Fetching order with order number: {}", orderNumber);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));

        return mapToResponse(order);
    }

    /**
     * Get orders by customer email
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByEmail(String email) {
        log.info("Fetching orders for email: {}", email);

        List<Order> orders = orderRepository.findByEmail(email);
        log.info("Found {} orders for email: {}", orders.size(), email);

        return orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update order status
     */
    @Override
    public OrderResponse updateOrderStatus(UUID orderId, Order.OrderStatus newStatus) {
        log.info("Updating order {} status to {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // TODO: Add status transition validation in Part 4
        // validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully");

        return mapToResponse(updatedOrder);
    }

    // ======================== HELPER METHODS ========================

    /**
     * Generate unique order number
     * Format: ORD-YYYYMMDD-HHMMSS-RANDOM
     * Example: ORD-20241214-143052-A4B9
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String orderNumber = String.format("ORD-%s-%s", timestamp, random);

        // Ensure uniqueness (very unlikely to collide, but good practice)
        while (orderRepository.existsByOrderNumber(orderNumber)) {
            random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            orderNumber = String.format("ORD-%s-%s", timestamp, random);
        }

        return orderNumber;
    }

    /**
     * Map Order entity to OrderResponse DTO
     */
    private OrderResponse mapToResponse(Order order) {
        List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemDto dto = new OrderItemDto();
                    dto.setItemId(item.getItemId());
                    dto.setProductId(item.getProductId());
                    dto.setProductName(item.getProductName());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    return dto;
                })
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getEmail(),
                order.getTotalAmount(),
                order.getStatus(),
                itemDtos,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getVersion()
        );
    }
}
