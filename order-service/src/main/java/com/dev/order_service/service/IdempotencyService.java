package com.dev.order_service.service;

import com.dev.order_service.repository.IdempotencyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository repository;

    @Scheduled(cron = "0 0 * * * *") // a cada hora
    public void cleanup() {
        repository.deleteByCreatedAtBefore(
                LocalDateTime.now().minusHours(24)
        );
    }
}
