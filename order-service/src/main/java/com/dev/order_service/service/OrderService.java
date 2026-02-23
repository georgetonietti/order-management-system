package com.dev.order_service.service;

import com.dev.order_service.client.ProductClient;
import com.dev.order_service.client.UserClient;
import com.dev.order_service.domain.Order;
import com.dev.order_service.domain.OrderStatus;
import com.dev.order_service.dto.CreateOrderRequest;
import com.dev.order_service.dto.OrderResponse;
import com.dev.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final UserClient userClient;
    private final ProductClient productClient;

    public OrderResponse createOrder(CreateOrderRequest request) {

        var user = userClient.getUserById(request.userId());
        var product = productClient.getProductById(request.productId());

        BigDecimal total = product.price().multiply(BigDecimal.valueOf(request.quantity()));

        Order order = Order.builder()
                .userId(user.id())
                .productId(product.id())
                .quantity(request.quantity())
                .totalPrice(total)
                .status(OrderStatus.CREATED)
                .build();

        Order savedOrder = repository.save(order);

        return mapToResponse(savedOrder);
    }

    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {

        Order order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().canTransitionTo(newStatus)) {
            throw new RuntimeException("Invalid status transition");
        }


        order.setStatus(newStatus);

        Order updated = repository.save(order);

        return mapToResponse(updated);
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
