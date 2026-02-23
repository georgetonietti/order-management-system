package com.dev.order_service.repository;

import com.dev.order_service.domain.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, UUID> {

    Optional<IdempotencyRecord> findByIdempotencyKeyAndUserId(String key, UUID userId);

    void deleteByCreatedAtBefore(LocalDateTime time);

}
