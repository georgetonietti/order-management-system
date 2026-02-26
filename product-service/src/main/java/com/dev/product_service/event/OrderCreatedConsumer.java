package com.dev.product_service.event;

import com.dev.product_service.dto.OrderCreatedEvent;
import com.dev.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ProductService productService;

    @RetryableTopic(
            attempts = "3",
            backoff = @org.springframework.retry.annotation.Backoff(
                    delay = 2000
            ),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = "order-created",
            groupId = "product-group"
    )
    public void consume(OrderCreatedEvent event) {

        log.info("Recebendo pedido {} para atualizar estoque", event.orderId());

        productService.processOrderEvent(event);
    }
}
