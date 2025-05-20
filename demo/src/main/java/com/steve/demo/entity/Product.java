package com.steve.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "flash_sale_start")
    private LocalDateTime flashSaleStart;

    @Column(name = "flash_sale_end")
    private LocalDateTime flashSaleEnd;

    @Column(name = "flash_sale_price")
    private BigDecimal flashSalePrice;

    @Column(name = "flash_sale_stock")
    private Integer flashSaleStock;

    @Version
    private Long version;
} 