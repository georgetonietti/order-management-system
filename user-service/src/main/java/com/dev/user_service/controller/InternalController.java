package com.dev.user_service.controller;

import com.dev.user_service.dto.UserResponse;
import com.dev.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    private final UserService userService;

    @GetMapping("users/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {

        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
}