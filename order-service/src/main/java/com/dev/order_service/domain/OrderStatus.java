package com.dev.order_service.domain;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    SHIPPED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case CREATED -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == SHIPPED;
            case SHIPPED, CANCELLED -> false;
        };
    }
}
