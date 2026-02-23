package com.dev.user_service.controller;

import com.dev.user_service.dto.LoginRequest;
import com.dev.user_service.dto.LoginResponse;
import com.dev.user_service.dto.RegisterRequest;
import com.dev.user_service.dto.UserResponse;
import com.dev.user_service.security.JwtService;
import com.dev.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        UserResponse user = userService.register(request.name(), request.email(), request.password());
        return ResponseEntity.ok(user);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserResponse user = userService.findByEmail(authentication.getName());
        String token = jwtService.generateToken(authentication);

        LoginResponse loginResponse = new LoginResponse(
                user.id(),
                user.name(),
                user.email(),
                token
        );


        return ResponseEntity.ok(loginResponse);
    }
}
