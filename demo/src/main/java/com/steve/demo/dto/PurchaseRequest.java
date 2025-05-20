package com.steve.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequest {
    @NotNull(message = "{validation.product.id.required}")
    private Long productId;

    @NotNull(message = "{validation.user.id.required}")
    private Long userId;

    @NotNull(message = "{validation.quantity.required}")
    @Min(value = 1, message = "{validation.quantity.positive}")
    private Integer quantity;
} 