package com.dev.user_service.service;

import com.dev.user_service.domain.Role;
import com.dev.user_service.domain.User;
import com.dev.user_service.dto.UserResponse;
import com.dev.user_service.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService service;

    @Test
    void shouldRegisterUser() {

        when(encoder.encode("123")).thenReturn("encodedPassword");

        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse user = service.register("John", "john@email.com", "123");

        assertThat(user.id()).isNotNull();
        assertThat(user.name()).isEqualTo("John");
        assertThat(user.email()).isEqualTo("john@email.com");
    }
}
