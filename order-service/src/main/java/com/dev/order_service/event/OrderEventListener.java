package com.dev.order_service.event;

import com.dev.order_service.dto.OrderCreatedDomainEvent;
import com.dev.order_service.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderEventProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCreatedDomainEvent event) {

        OrderCreatedEvent kafkaEvent = new OrderCreatedEvent(
                event.orderId(),
                event.userId(),
                event.productId(),
                event.quantity(),
                event.totalPrice()
        );

        producer.publish(kafkaEvent);
    }

}
