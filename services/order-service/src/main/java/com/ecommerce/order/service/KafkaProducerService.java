package com.ecommerce.order.service;

import com.ecommerce.order.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing events to Kafka
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Value("${kafka.topic.order-created}")
    private String orderCreatedTopic;

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publish order created event to Kafka
     */
    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        logger.info("Publishing order created event: Order ID = {}, Order Number = {}",
                event.getOrderId(), event.getOrderNumber());

        CompletableFuture<SendResult<String, OrderCreatedEvent>> future =
                kafkaTemplate.send(orderCreatedTopic, event.getOrderId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Successfully published order created event: Order ID = {}, Offset = {}",
                        event.getOrderId(), result.getRecordMetadata().offset());
            } else {
                logger.error("Failed to publish order created event: Order ID = {}",
                        event.getOrderId(), ex);
            }
        });
    }
}
