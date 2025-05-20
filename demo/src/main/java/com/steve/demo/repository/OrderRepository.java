package com.steve.demo.repository;

import com.steve.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
} 