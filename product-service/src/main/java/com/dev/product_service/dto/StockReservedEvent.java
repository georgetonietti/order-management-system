package com.dev.product_service.dto;

import java.util.UUID;

public record StockReservedEvent(
        Long orderId,
        UUID productId,
        Integer quantity
) {
}
