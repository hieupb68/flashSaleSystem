package com.steve.demo.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steve.demo.entity.Order;
import com.steve.demo.entity.OrderStatus;
import com.steve.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProcessor {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String DLQ_TOPIC = "flash-sale-dlq";
    private static final int MAX_RETRIES = 3;

    @KafkaListener(topics = "${app.kafka.topic.flash-sale}", groupId = "flash-sale-group")
    @Transactional
    public void processOrder(String orderJson) {
        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            log.info("Processing order: {}", order.getId());

            // Update order status to PROCESSING
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);

            // Simulate some processing time
            Thread.sleep(1000);

            // Update order status to COMPLETED
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            log.info("Order processed successfully: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to process order", e);
            handleFailedOrder(orderJson, e);
        }
    }

    private void handleFailedOrder(String orderJson, Exception e) {
        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            
            // Update order status to FAILED
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            // Send to DLQ for retry
            kafkaTemplate.send(DLQ_TOPIC, orderJson);
            log.info("Order sent to DLQ: {}", order.getId());
        } catch (Exception ex) {
            log.error("Failed to handle failed order", ex);
        }
    }

    @KafkaListener(topics = DLQ_TOPIC, groupId = "flash-sale-dlq-group")
    @Transactional
    public void processDLQ(String orderJson) {
        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            log.info("Processing DLQ order: {}", order.getId());

            // Implement retry logic here
            // For example, you could:
            // 1. Check retry count
            // 2. Implement exponential backoff
            // 3. Send to another queue for manual review
            // 4. Notify administrators

            // For now, we'll just log it
            log.info("DLQ order needs manual review: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to process DLQ order", e);
        }
    }
} 