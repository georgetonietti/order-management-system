package com.dev.product_service.event;

import com.dev.product_service.dto.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;

public class OrderCreatedDLTConsumer {

    @KafkaListener(
            topics = "order-created.DLT",
            groupId = "product-group-dlt"
    )
    public void consumeDLT(OrderCreatedEvent event) {

        System.out.println("Mensagem enviada para DLQ. Pedido: " + event.orderId());

        // Aqui você pode:
        // - salvar em tabela de erro
        // - notificar time
        // - enviar alerta
    }
}
