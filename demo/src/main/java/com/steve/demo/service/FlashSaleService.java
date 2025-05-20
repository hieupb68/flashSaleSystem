package com.steve.demo.service;

import com.steve.demo.dto.FlashSaleRequest;
import com.steve.demo.entity.Order;
import com.steve.demo.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface FlashSaleService {
    Order processFlashSale(FlashSaleRequest request);
    void initializeProductStock(Long productId, Integer stock);
    List<Product> getActiveFlashSales();
    List<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice);
    List<Order> getUserOrders(Long userId);
} 