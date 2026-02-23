package com.dev.order_service.domain;

public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case CREATED -> newStatus == PAID || newStatus == CANCELLED;
            case PAID -> newStatus == SHIPPED;
            case SHIPPED, CANCELLED -> false;
        };
    }
}
