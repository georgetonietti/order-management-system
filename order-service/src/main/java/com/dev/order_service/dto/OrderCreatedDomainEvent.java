package com.dev.order_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedDomainEvent(
        Long orderId,
        UUID userId,
        UUID productId,
        Integer quantity,
        BigDecimal totalPrice
) {
}
