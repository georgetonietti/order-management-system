package com.dev.order_service.dto;

import com.dev.order_service.domain.OrderStatus;

public record UpdateOrderStatusRequest(
        OrderStatus status
) {
}
