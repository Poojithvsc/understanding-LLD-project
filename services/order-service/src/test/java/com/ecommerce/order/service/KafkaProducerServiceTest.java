package com.ecommerce.order.service;

import com.ecommerce.order.event.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for KafkaProducerService
 *
 * What we're testing:
 * - Kafka event publishing logic
 * - Using Mockito to mock KafkaTemplate (no real Kafka broker needed)
 * - Verifying events are sent to the correct topic with correct data
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaProducerService Unit Tests")
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private OrderCreatedEvent testEvent;
    private final String TEST_TOPIC = "order-created";

    @BeforeEach
    void setUp() {
        // Set the topic name using ReflectionTestUtils since it's a @Value field
        ReflectionTestUtils.setField(kafkaProducerService, "orderCreatedTopic", TEST_TOPIC);

        // Create test event
        UUID orderId = UUID.randomUUID();
        List<OrderCreatedEvent.OrderItemEvent> items = new ArrayList<>();
        items.add(new OrderCreatedEvent.OrderItemEvent(
                UUID.randomUUID(),
                "Test Product",
                2,
                new BigDecimal("99.99")
        ));

        testEvent = new OrderCreatedEvent(
                orderId,
                "ORD-20251216-TEST",
                "Test User",
                "test@example.com",
                new BigDecimal("199.98"),
                "PENDING",
                items
        );
    }

    @Test
    @DisplayName("Should successfully publish order created event to Kafka")
    void testPublishOrderCreatedEvent_Success() {
        // Arrange
        CompletableFuture<SendResult<String, OrderCreatedEvent>> future =
                CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(eq(TEST_TOPIC), eq(testEvent.getOrderId().toString()), eq(testEvent)))
                .thenReturn(future);

        // Act
        kafkaProducerService.publishOrderCreatedEvent(testEvent);

        // Assert
        verify(kafkaTemplate, times(1)).send(
                eq(TEST_TOPIC),
                eq(testEvent.getOrderId().toString()),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should call KafkaTemplate send with correct topic")
    void testPublishOrderCreatedEvent_CorrectTopic() {
        // Arrange
        CompletableFuture<SendResult<String, OrderCreatedEvent>> future =
                CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(anyString(), anyString(), any(OrderCreatedEvent.class)))
                .thenReturn(future);

        // Act
        kafkaProducerService.publishOrderCreatedEvent(testEvent);

        // Assert
        verify(kafkaTemplate).send(
                eq("order-created"),
                anyString(),
                any(OrderCreatedEvent.class)
        );
    }

    @Test
    @DisplayName("Should use order ID as message key")
    void testPublishOrderCreatedEvent_UsesOrderIdAsKey() {
        // Arrange
        CompletableFuture<SendResult<String, OrderCreatedEvent>> future =
                CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(anyString(), anyString(), any(OrderCreatedEvent.class)))
                .thenReturn(future);

        // Act
        kafkaProducerService.publishOrderCreatedEvent(testEvent);

        // Assert
        verify(kafkaTemplate).send(
                anyString(),
                eq(testEvent.getOrderId().toString()),
                any(OrderCreatedEvent.class)
        );
    }

    @Test
    @DisplayName("Should send the complete event object")
    void testPublishOrderCreatedEvent_SendsCompleteEvent() {
        // Arrange
        CompletableFuture<SendResult<String, OrderCreatedEvent>> future =
                CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(anyString(), anyString(), any(OrderCreatedEvent.class)))
                .thenReturn(future);

        // Act
        kafkaProducerService.publishOrderCreatedEvent(testEvent);

        // Assert
        verify(kafkaTemplate).send(
                anyString(),
                anyString(),
                eq(testEvent)
        );
    }

    @Test
    @DisplayName("Should handle multiple events independently")
    void testPublishOrderCreatedEvent_MultipleEvents() {
        // Arrange
        OrderCreatedEvent event1 = testEvent;
        OrderCreatedEvent event2 = new OrderCreatedEvent(
                UUID.randomUUID(),
                "ORD-20251216-TEST2",
                "Test User 2",
                "test2@example.com",
                new BigDecimal("299.99"),
                "PENDING",
                new ArrayList<>()
        );

        CompletableFuture<SendResult<String, OrderCreatedEvent>> future =
                CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(anyString(), anyString(), any(OrderCreatedEvent.class)))
                .thenReturn(future);

        // Act
        kafkaProducerService.publishOrderCreatedEvent(event1);
        kafkaProducerService.publishOrderCreatedEvent(event2);

        // Assert
        verify(kafkaTemplate, times(2)).send(
                anyString(),
                anyString(),
                any(OrderCreatedEvent.class)
        );

        verify(kafkaTemplate).send(
                eq(TEST_TOPIC),
                eq(event1.getOrderId().toString()),
                eq(event1)
        );

        verify(kafkaTemplate).send(
                eq(TEST_TOPIC),
                eq(event2.getOrderId().toString()),
                eq(event2)
        );
    }
}
