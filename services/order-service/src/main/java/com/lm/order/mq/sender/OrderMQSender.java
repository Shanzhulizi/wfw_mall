package com.lm.order.mq.sender;

import com.lm.order.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.lm.order.domain.Order;
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMQSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderCreateMsg(Order order) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                order
        );
        log.info("发送订单创建消息: {}", order.getOrderNo());
    }
}

