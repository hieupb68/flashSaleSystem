package com.steve.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "{validation.name.required}")
    @Size(min = 3, max = 100, message = "{validation.name.length}")
    private String name;

    @NotBlank(message = "{validation.description.required}")
    @Size(min = 10, max = 500, message = "{validation.description.length}")
    private String description;

    @NotNull(message = "{validation.price.required}")
    @Min(value = 0, message = "{validation.price.positive}")
    private BigDecimal price;

    @NotNull(message = "{validation.stock.required}")
    @Min(value = 0, message = "{validation.stock.positive}")
    private Integer stock;
} 