package com.steve.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steve.demo.dto.FlashSaleRequest;
import com.steve.demo.entity.Order;
import com.steve.demo.entity.OrderStatus;
import com.steve.demo.entity.Product;
import com.steve.demo.repository.OrderRepository;
import com.steve.demo.repository.ProductRepository;
import com.steve.demo.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashSaleServiceImpl implements FlashSaleService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> flashSaleScript;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Sliding window rate limiting per user
    private final Map<Long, Queue<Long>> userActivityWindow = new ConcurrentHashMap<>();
    private static final int WINDOW_SIZE = 1000; // 1 second
    private static final int MAX_REQUESTS = 10;  // Max 10 requests per second

    @Override
    @Transactional
    public Order processFlashSale(FlashSaleRequest request) {
        // Check user rate limit
        if (!isWithinRateLimit(request.getUserId())) {
            throw new RuntimeException("Rate limit exceeded");
        }

        // Execute Redis Lua script for atomic operation
        List<String> keys = Arrays.asList(
            "product:stock:" + request.getProductId(),
            "product:purchased:" + request.getProductId()
        );
        
        Long result = redisTemplate.execute(
            flashSaleScript,
            keys,
            request.getUserId().toString(),
            request.getQuantity().toString()
        );

        if (result == null || result < 0) {
            throw new RuntimeException(
                result == -1 ? "User has already purchased" :
                result == -2 ? "Not enough stock" :
                "Flash sale processing failed"
            );
        }

        // Create order
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.getFlashSalePrice().multiply(new BigDecimal(request.getQuantity())));
        order.setStatus(OrderStatus.PENDING);

        // Save order to database
        order = orderRepository.save(order);

        // Send to Kafka for async processing
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("flash-sale-orders", orderJson);
        } catch (Exception e) {
            log.error("Failed to send order to Kafka", e);
            throw new RuntimeException("Failed to process order");
        }

        return order;
    }

    @Override
    public void initializeProductStock(Long productId, Integer stock) {
        String stockKey = "product:stock:" + productId;
        String purchasedKey = "product:purchased:" + productId;
        
        redisTemplate.delete(Arrays.asList(stockKey, purchasedKey));
        redisTemplate.opsForValue().set(stockKey, stock.toString());
    }

    @Override
    public List<Product> getActiveFlashSales() {
        return productRepository.findActiveFlashSales(LocalDateTime.now());
    }

    @Override
    public List<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = BigDecimal.valueOf(Long.MAX_VALUE);
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private boolean isWithinRateLimit(Long userId) {
        long currentTime = System.currentTimeMillis();
        Queue<Long> userWindow = userActivityWindow.computeIfAbsent(userId, k -> new LinkedList<>());
        
        // Remove old requests
        while (!userWindow.isEmpty() && currentTime - userWindow.peek() > WINDOW_SIZE) {
            userWindow.poll();
        }
        
        // Check request count in window
        if (userWindow.size() >= MAX_REQUESTS) {
            return false;
        }
        
        // Add new request
        userWindow.offer(currentTime);
        return true;
    }
} 