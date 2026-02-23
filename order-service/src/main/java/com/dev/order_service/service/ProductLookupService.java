package com.dev.order_service.service;

import com.dev.order_service.client.ProductClient;

import com.dev.order_service.dto.ProductResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProductLookupService {

    private final ProductClient productClient;

    @Cacheable(value = "products", key = "#id")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallback")
    public ProductResponse getProduct(UUID id) {
        return productClient.getProductById(id);
    }

    private ProductResponse fallback(UUID id, Throwable ex) {
        throw new RuntimeException("Product service unavailable");
    }
}