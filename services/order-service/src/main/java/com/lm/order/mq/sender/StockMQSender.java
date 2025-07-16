package com.lm.order.mq.sender;

import com.lm.mq.StockDeductMessage;
import com.lm.order.domain.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.lm.common.constant.MQConstant.STOCK_DEDUCT_ROUTING_KEY;
import static com.lm.common.constant.MQConstant.STOCK_EVENT_EXCHANGE;

@Slf4j
@Component
public class StockMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送扣减库存消息
     */
    public void sendStockDeductMessage(String orderNo, List<OrderItem> items) {
        List<StockDeductMessage.StockItem> stockItems = items.stream()
                .map(item -> new StockDeductMessage.StockItem(item.getSkuId(), item.getQuantity()))
                .collect(Collectors.toList());

        StockDeductMessage message = new StockDeductMessage(orderNo, stockItems);
        log.info("发送消息：{}",message);
        rabbitTemplate.convertAndSend(STOCK_EVENT_EXCHANGE, STOCK_DEDUCT_ROUTING_KEY, message);
    }
}
