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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final UserClient userClient;
    private final ProductLookupService poductLookupService;
    private final IdempotencyService idempotencyService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String idempotencyKey) {

        UUID userId = request.userId();

        Optional<Long> redisHit = idempotencyService.checkRedis(userId, idempotencyKey);

        if (redisHit.isPresent()) {
            return repository.findById(redisHit.get())
                    .map(this::mapToResponse)
                    .orElseThrow();
        }

        try {
            var user = getUser(userId);
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

            idempotencyService.save(userId, idempotencyKey, savedOrder.getId());

            return mapToResponse(savedOrder);

        } catch (DataIntegrityViolationException ex) {

            var record = idempotencyService
                    .findByIdempotencyKeyAndUserId(idempotencyKey, userId)
                    .orElseThrow();

            idempotencyService.saveOnRedis(userId, idempotencyKey, record.getOrderId());

            return repository.findById(record.getOrderId())
                    .map(this::mapToResponse)
                    .orElseThrow();
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
