package com.lm.order.mq.listener;

import com.lm.order.domain.Order;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@RocketMQMessageListener(topic = "PAYMENT_TOPIC", consumerGroup = "order-payment-group")
@Component
public class PaymentSuccessListener implements RocketMQListener<String> {

//    @Autowired
//    OrderRepository orderRepository;
//
    @Override
    public void onMessage(String payload) {
//        Map<String,Object> body = parseJson(payload);
//        String orderNo = (String) body.get("orderNo");
//        // 幂等：只在订单状态为 PENDING_PAYMENT 时更新为 PAID (乐观锁)
//        Order order = orderRepository.findByOrderNo(orderNo);
//        if (order == null) return;
//        if (!"PENDING_PAYMENT".equals(order.getStatus())) return;
//        order.setStatus("PAID");
//        orderRepository.save(order);
//        // 触发后续（扣库存/发货等），可再发消息或直接调用
    }
}
