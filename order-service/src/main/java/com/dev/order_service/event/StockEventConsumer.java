package com.dev.order_service.event;

import com.dev.order_service.domain.OrderStatus;
import com.dev.order_service.dto.StockFailedEvent;
import com.dev.order_service.dto.StockReservedEvent;
import com.dev.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "stock-reserved")
    public void handleStockReserved(StockReservedEvent event) {

        orderService.updateStatus(event.orderId(), OrderStatus.CONFIRMED);
    }

    @KafkaListener(topics = "stock-failed")
    public void handleStockFailed(StockFailedEvent event) {

        orderService.updateStatus(event.orderId(), OrderStatus.CANCELLED);
    }
}
