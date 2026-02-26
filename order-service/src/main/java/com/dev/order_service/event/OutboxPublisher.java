package com.dev.order_service.event;

import com.dev.order_service.domain.OutboxEvent;
import com.dev.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEvent> events = repository.findByProcessedFalse();

        for (OutboxEvent event : events) {

            kafkaTemplate.send("order-created", event.getPayload());

            event.setProcessed(true);
            repository.save(event);
        }
    }
}
