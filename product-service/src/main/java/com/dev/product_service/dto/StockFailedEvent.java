package com.dev.product_service.dto;

public record StockFailedEvent(
        Long orderId,
        String reason
) {
}
