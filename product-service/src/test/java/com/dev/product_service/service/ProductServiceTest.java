package com.dev.product_service.service;

import com.dev.product_service.domain.Product;
import com.dev.product_service.dto.ProductRequest;
import com.dev.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    void shouldCreateProduct() {
        ProductRequest request =
                new ProductRequest("Notebook", "Dell XPS", BigDecimal.valueOf(8000), 10);

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build();

        when(repository.save(any(Product.class))).thenReturn(product);

        var response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Notebook");
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldFindProductById() {
        UUID id = UUID.randomUUID();

        Product product = Product.builder()
                .id(id)
                .name("Mouse")
                .price(BigDecimal.valueOf(200))
                .stock(20)
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(product));

        var response = service.findById(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.name()).isEqualTo("Mouse");
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }
}
