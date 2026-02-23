package com.dev.order_service.controller;


import com.dev.order_service.dto.CreateOrderRequest;
import com.dev.order_service.dto.OrderResponse;
import com.dev.order_service.dto.UpdateOrderStatusRequest;
import com.dev.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public OrderResponse create(@RequestBody @Valid CreateOrderRequest request) {
        return service.createOrder(request);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(service.updateStatus(id, request.status()));
    }
}
