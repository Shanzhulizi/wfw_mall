package com.lm.order.mq.listener;

import com.lm.order.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.lm.common.constant.MQConstant.ORDER_CREATE_QUEUE;

@Slf4j
@Component
public class OrderMQListener {

    @RabbitListener(queues = ORDER_CREATE_QUEUE)
    public void handleOrderCreate(Order order) {
        log.info("收到订单创建消息: {}", order.getOrderNo());
        // 落库、通知其他服务等
    }
}
