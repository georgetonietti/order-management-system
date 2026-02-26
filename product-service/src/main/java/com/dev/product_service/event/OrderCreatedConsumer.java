package com.dev.product_service.event;

import com.dev.product_service.dto.OrderCreatedEvent;
import com.dev.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProductService productService;

    @KafkaListener(
            topics = "order-created",
            groupId = "product-group"
    )
    public void consume(OrderCreatedEvent event) {

        log.info("Recebendo pedido {} para atualizar estoque", event.orderId());

        productService.decreaseStock(
                event.productId(),
                event.quantity()
        );
    }
}
