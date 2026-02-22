package com.dev.user_service.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtService jwtService;

    @Test
    void shouldGeneration() {

        var auth = new UsernamePasswordAuthenticationToken(
                "john@email.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // 👇 Mock do retorno do encoder
        Jwt jwt = new Jwt(
                "fake-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("alg", "HS256"),
                Map.of("sub", "john@email.com")
        );

        when(jwtEncoder.encode(any())).thenReturn(jwt);

        String token = jwtService.generateToken(auth);

        assertThat(token).isNotBlank();
        assertThat(token).isEqualTo("fake-token");
    }
}
