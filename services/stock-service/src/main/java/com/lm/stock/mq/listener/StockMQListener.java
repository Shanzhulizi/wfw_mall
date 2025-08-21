package com.lm.stock.mq.listener;

import com.lm.mq.StockDeductMessage;
import com.lm.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.lm.common.constant.MQConstant.STOCK_DEDUCT_QUEUE;
@Slf4j
@Component
public class StockMQListener {
    @Autowired
    private StockService stockService;

    @RabbitListener(queues = STOCK_DEDUCT_QUEUE)
    public void onMessage(StockDeductMessage message) {
        log.info("收到订单扣库存消息：{}", message);
        for (StockDeductMessage.StockItem item : message.getItems()) {
            try {
                stockService.deductDBStock(item.getSkuId(), item.getQuantity());
            } catch (Exception e) {
                log.error("扣减 SKU[{}] 库存失败：{}", item.getSkuId(), e.getMessage());
                // TODO: 你可以考虑记录失败项、发补偿消息、存 Redis 补偿表
                // 可发送到死信队列或重试队列
//                不要回滚，因为这是异步操作，失败记录下次补偿
            }
        }
    }

}
