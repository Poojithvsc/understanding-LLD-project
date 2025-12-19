package com.ecommerce.inventory.service;

import com.ecommerce.inventory.event.OrderCreatedEvent;
import com.ecommerce.inventory.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for consuming events from Kafka
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final InventoryService inventoryService;

    @Autowired
    public KafkaConsumerService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Listen for order created events and update inventory
     */
    @KafkaListener(topics = "${kafka.topic.order-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        logger.info("Received order created event: Order ID = {}, Order Number = {}",
                event.getOrderId(), event.getOrderNumber());

        // Process each item in the order
        for (OrderCreatedEvent.OrderItemEvent item : event.getOrderItems()) {
            // Get the product
            Product product = inventoryService.getProductById(item.getProductId());

            // Calculate new stock quantity
            int currentStock = product.getStockQuantity();
            int newStock = currentStock - item.getQuantity();

            if (newStock < 0) {
                logger.warn("Insufficient stock for product {}: requested = {}, available = {}",
                        item.getProductId(), item.getQuantity(), currentStock);
                // In a real system, you might want to:
                // 1. Send a notification
                // 2. Cancel the order
                // 3. Put it in a pending state
                // For now, we'll just set it to 0
                newStock = 0;
            }

            // Update product stock
            product.setStockQuantity(newStock);
            inventoryService.updateProduct(item.getProductId(), product);

            logger.info("Updated stock for product {}: {} -> {} (decreased by {})",
                    item.getProductId(), currentStock, newStock, item.getQuantity());
        }

        logger.info("Successfully processed order created event for order: {}", event.getOrderId());
    }
}
