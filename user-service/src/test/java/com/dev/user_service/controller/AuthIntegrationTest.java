package com.dev.user_service.controller;

import com.dev.user_service.BaseIntegrationTest;
import com.dev.user_service.dto.LoginRequest;
import com.dev.user_service.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldLoginSuccessfully() {

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RegisterRequest registerRequest = new RegisterRequest("John","john@email.com", "123456");
        var registerEntity = new HttpEntity<>(registerRequest, headers);

        restTemplate.postForEntity("/auth/register", registerEntity, String.class);

        LoginRequest loginRequest = new LoginRequest("john@email.com", "123456");
        var loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity("/auth/login", loginEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("access_token");
    }
}
