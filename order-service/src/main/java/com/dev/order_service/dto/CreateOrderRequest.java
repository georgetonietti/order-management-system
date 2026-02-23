package com.dev.order_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateOrderRequest(
        @NotNull
        UUID userId,

        @NotNull
        UUID productId,

        @Positive
        Integer quantity

) {
}
