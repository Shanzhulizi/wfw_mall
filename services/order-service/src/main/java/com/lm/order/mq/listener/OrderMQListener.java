package com.lm.order.mq.listener;

import com.lm.order.config.RabbitMQConfig;
import com.lm.order.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderMQListener {

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderCreate(Order order) {
        log.info("收到订单创建消息: {}", order.getOrderNo());
        // 落库、通知其他服务等
    }
}
