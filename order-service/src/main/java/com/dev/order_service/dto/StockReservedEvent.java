package com.dev.order_service.dto;

import java.util.UUID;

public record StockReservedEvent(
        Long orderId,
        UUID productId,
        Integer quantity
) {
}
