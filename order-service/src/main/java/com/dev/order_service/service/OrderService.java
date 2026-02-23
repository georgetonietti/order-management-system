package com.dev.order_service.service;

import com.dev.order_service.client.UserClient;
import com.dev.order_service.domain.IdempotencyRecord;
import com.dev.order_service.domain.Order;
import com.dev.order_service.domain.OrderStatus;
import com.dev.order_service.dto.CreateOrderRequest;
import com.dev.order_service.dto.OrderResponse;

import com.dev.order_service.dto.UserResponse;
import com.dev.order_service.repository.IdempotencyRecordRepository;
import com.dev.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final UserClient userClient;
    private final ProductLookupService poductLookupService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey) {

        UUID userId = request.userId();

        var existingOrder = idempotencyRecordRepository.findByIdempotencyKeyAndUserId(idempotencyKey, userId);

        if (existingOrder.isPresent()) {
            Order order = repository.findById(existingOrder.get().getOrderId()).orElseThrow();

            return mapToResponse(order);
        }

        try {
            var user = getUser(request.userId());
            var product = poductLookupService.getProduct(request.productId());

            BigDecimal total = product.price().multiply(BigDecimal.valueOf(request.quantity()));

            Order order = Order.builder()
                    .userId(user.id())
                    .productId(product.id())
                    .quantity(request.quantity())
                    .totalPrice(total)
                    .status(OrderStatus.CREATED)
                    .build();

            Order savedOrder = repository.save(order);

            idempotencyRecordRepository.save(
                    IdempotencyRecord.builder()
                            .idempotencyKey(idempotencyKey)
                            .userId(userId)
                            .orderId(savedOrder.getId())
                            .build()
            );

            return mapToResponse(savedOrder);
        } catch (DataIntegrityViolationException ex) {

            var record = idempotencyRecordRepository
                    .findByIdempotencyKeyAndUserId(idempotencyKey, userId)
                    .orElseThrow();

            var order = repository
                    .findById(record.getOrderId())
                    .orElseThrow();

            return mapToResponse(order);
        }
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

    @CircuitBreaker(name = "userService", fallbackMethod = "userFallback")
    private UserResponse getUser(UUID id) {
        return userClient.getUserById(id);
    }

    private UserResponse userFallback(UUID id, Throwable ex) {
        throw new RuntimeException("User service unavailable");
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
