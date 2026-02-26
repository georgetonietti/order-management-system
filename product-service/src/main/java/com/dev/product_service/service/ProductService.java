package com.dev.product_service.service;

import com.dev.product_service.domain.ProcessedEvent;
import com.dev.product_service.domain.Product;
import com.dev.product_service.dto.*;
import com.dev.product_service.event.StockEventProducer;
import com.dev.product_service.repository.ProcessedEventRepository;
import com.dev.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProcessedEventRepository processedEventRepository;
    private final StockEventProducer stockEventProducer;

    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build();

        Product savedProduct = repository.save(product);

        return mapToResponse(savedProduct);
    }

    public List<ProductResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse findById(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return mapToResponse(product);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public void decreaseStock(UUID productId, Integer quantity) {

        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Estoque insuficiente");
        }

        product.setStock(product.getStock() - quantity);

        repository.save(product);
    }

    @Transactional
    public void processOrderEvent(OrderCreatedEvent event) {
        try {
            decreaseStock(event.productId(), event.quantity());

            processedEventRepository.save(
                    ProcessedEvent.builder()
                            .eventId(event.orderId().toString())
                            .processedAt(LocalDateTime.now())
                            .build()
            );

            stockEventProducer.publishReserved(
                    new StockReservedEvent(
                            event.orderId(),
                            event.productId(),
                            event.quantity()
                    )
            );

        } catch (DataIntegrityViolationException e) {

            System.out.println("Evento já processado: " + event.orderId());

        } catch (IllegalStateException e) {

            stockEventProducer.publishFailed(
                    new StockFailedEvent(
                            event.orderId(),
                            e.getMessage()
                    )
            );

        }
    }




    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt()
        );
    }
}
