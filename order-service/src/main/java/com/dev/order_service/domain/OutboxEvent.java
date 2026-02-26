package com.dev.order_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String aggregateType; // ORDER
    private String aggregateId;   // orderId

    @Enumerated(EnumType.STRING)
    private OrderStatus type;          // ORDER_CREATED

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime createdAt;

    private boolean processed;
}
