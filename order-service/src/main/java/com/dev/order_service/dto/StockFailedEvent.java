package com.dev.order_service.dto;

public record StockFailedEvent(
        Long orderId,
        String reason
) {
}
