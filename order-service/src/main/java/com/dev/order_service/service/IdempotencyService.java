package com.dev.order_service.service;

import com.dev.order_service.domain.IdempotencyRecord;
import com.dev.order_service.repository.IdempotencyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private final IdempotencyRecordRepository repository;

    private static final Duration TTL = Duration.ofHours(24);

    public Optional<Long> checkRedis(UUID userId, String key) {
        String redisKey = buildKey(userId, key);
        String value = redisTemplate.opsForValue().get(redisKey);

        return value != null ? Optional.of(Long.valueOf(value)) : Optional.empty();
    }

    public void save(UUID userId, String key, Long orderId) {

        IdempotencyRecord idempotencyRecord  = IdempotencyRecord.builder()
                                                .idempotencyKey(key)
                                                .userId(userId)
                                                .orderId(orderId)
                                                .build();

        repository.save(idempotencyRecord);

        redisTemplate.opsForValue().set(
                buildKey(userId, key),
                orderId.toString(),
                TTL
        );
    }

    public void saveOnRedis(UUID userId, String key, Long orderId) {
        redisTemplate.opsForValue().set(
                buildKey(userId, key),
                orderId.toString(),
                TTL
        );
    }

    public Optional<IdempotencyRecord> findByIdempotencyKeyAndUserId(String idempotencyKey, UUID userId) {
        return repository.findByIdempotencyKeyAndUserId(idempotencyKey, userId);
    }

    private String buildKey(UUID userId, String key) {
        return "idempotency:" + userId + ":" + key;
    }
}
