package com.lm.order.mq.sender;

import com.lm.mq.StockDeductMessage;
import com.lm.order.config.RabbitMQConfig;
import com.lm.order.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.lm.order.domain.Order;

import java.util.List;
import java.util.stream.Collectors;

import static com.lm.common.constant.MQConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送创建订单消息
     */
    public void sendCreateOrderMessage(Order order) {
        log.info("发送订单创建消息：{}", order.getOrderNo());

        rabbitTemplate.convertAndSend(ORDER_CREATE_EXCHANGE, ORDER_CREATE_ROUTING_KEY, order);
    }
}

