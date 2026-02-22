package com.dev.user_service.service;

import com.dev.user_service.domain.Role;
import com.dev.user_service.domain.User;
import com.dev.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public User register(String name, String email, String password) {

        if(repository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode(password))
                .role(Role.ROLE_USER)
                .build();

        return repository.save(user);
    }
}
