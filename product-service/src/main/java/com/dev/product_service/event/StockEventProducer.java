package com.dev.product_service.event;

import com.dev.product_service.dto.StockFailedEvent;
import com.dev.product_service.dto.StockReservedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishReserved(StockReservedEvent event) {
        kafkaTemplate.send("stock-reserved", event);
    }

    public void publishFailed(StockFailedEvent event) {
        kafkaTemplate.send("stock-failed", event);
    }
}
