package com.dev.product_service.controller;

import com.dev.product_service.BaseIntegrationTest;
import com.dev.product_service.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateProduct() {

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductRequest request =
                new ProductRequest("Keyboard", "Mechanical", BigDecimal.valueOf(500), 15);

        var entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/products", entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("Keyboard");
    }
}