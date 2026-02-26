package com.dev.product_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        Long orderId,
        UUID userId,
        UUID productId,
        Integer quantity,
        BigDecimal totalPrice
) {
}
