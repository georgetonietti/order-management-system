package com.dev.user_service.dto;

import java.util.UUID;

public record LoginResponse(
        UUID id,
        String name,
        String email,
        String token
) {
}
