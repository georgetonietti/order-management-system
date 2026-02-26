package com.dev.product_service.service;

import com.dev.product_service.domain.Product;
import com.dev.product_service.dto.ProductRequest;
import com.dev.product_service.dto.ProductResponse;
import com.dev.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

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

    @Transactional
    public void decreaseStock(UUID productId, Integer quantity) {

        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Estoque insuficiente");
        }

        product.setStock(product.getStock() - quantity);

        repository.save(product);
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
