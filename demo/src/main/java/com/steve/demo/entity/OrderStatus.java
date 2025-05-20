package com.steve.demo.entity;

public enum OrderStatus {
    PENDING,    // Order is created but not yet processed
    PROCESSING, // Order is being processed
    COMPLETED,  // Order is successfully completed
    FAILED,     // Order processing failed
    CANCELLED   // Order was cancelled
} 