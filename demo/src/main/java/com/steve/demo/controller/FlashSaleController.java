package com.steve.demo.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.steve.demo.dto.FlashSaleRequest;
import com.steve.demo.entity.Order;
import com.steve.demo.entity.Product;
import com.steve.demo.service.FlashSaleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flash-sale")
@RequiredArgsConstructor
public class FlashSaleController {

    private final FlashSaleService flashSaleService;
    private final RateLimiter rateLimiter = RateLimiter.create(100.0); // 100 requests per second
    private final MessageSource messageSource;

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@Valid @RequestBody FlashSaleRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                String message = messageSource.getMessage(error.getDefaultMessage(), null, LocaleContextHolder.getLocale());
                errors.put(error.getField(), message);
            }
            return ResponseEntity.badRequest().body(errors);
        }

        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        try {
            Order order = flashSaleService.processFlashSale(request);
            String message = messageSource.getMessage("success.order.created", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(Map.of("message", message, "order", order));
        } catch (Exception e) {
            String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            return ResponseEntity.badRequest().body(Map.of("error", message));
        }
    }

    @PostMapping("/products/{id}/initialize")
    public ResponseEntity<?> initializeProduct(@PathVariable Long id, @RequestParam @Valid @Min(1) Integer stock) {
        try {
            flashSaleService.initializeProductStock(id, stock);
            String message = messageSource.getMessage("success.product.initialized", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            return ResponseEntity.badRequest().body(Map.of("error", message));
        }
    }

    @GetMapping("/products/active")
    public ResponseEntity<List<Product>> getActiveFlashSales() {
        return ResponseEntity.ok(flashSaleService.getActiveFlashSales());
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(flashSaleService.searchProducts(minPrice, maxPrice));
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(flashSaleService.getUserOrders(userId));
        } catch (Exception e) {
            String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            return ResponseEntity.badRequest().body(Map.of("error", message));
        }
    }
} 