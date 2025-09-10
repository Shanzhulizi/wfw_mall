package com.lm.order.mq.consumer;

import com.lm.common.R;
import com.lm.message.OrderTimeoutMessage;
import com.lm.order.Eumn.OrderStatus;
import com.lm.order.domain.OrderItem;
import com.lm.order.feign.CouponFeignClient;
import com.lm.order.feign.StockFeignClient;
import com.lm.order.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RocketMQMessageListener(
        topic = "ORDER_TIMEOUT_TOPIC",
        selectorExpression = "ORDER_TIMEOUT",
        consumerGroup = "order_timeout_consumer_group"
)
public class OrderTimeoutConsumer implements RocketMQListener<OrderTimeoutMessage> {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StockFeignClient stockFeignClient;

    @Autowired
    private CouponFeignClient couponFeignClient;

    @Override
    public void onMessage(OrderTimeoutMessage message) {
        String orderNo = message.getOrderNo();
        log.info("处理订单超时，orderNo: {}", orderNo);

        try {
            // 1. 检查订单状态
            Integer status = orderMapper.selectStatusByOrderNo(orderNo);
            if (status == null) {
                log.warn("订单不存在，orderNo: {}", orderNo);
                return;
            }

            // 2. 只有待支付状态的订单才需要超时处理
            if (status != OrderStatus.WAITING_PAY.getCode()) {
                log.info("订单状态不是待支付，跳过超时处理，orderNo: {}, status: {}", orderNo, status);
                return;
            }

            // 3. 更新订单状态为超时关闭
            int updateCount = orderMapper.updateStatus(orderNo, OrderStatus.PAY_TIMEOUT.getCode());
            if (updateCount == 0) {
                log.warn("更新订单状态失败，orderNo: {}", orderNo);
                return;
            }

            // 4. 释放库存
            releaseStock(orderNo);

            // 5. 释放优惠券
            releaseCoupons(orderNo);

            log.info("订单超时处理完成，orderNo: {}", orderNo);

        } catch (Exception e) {
            log.error("订单超时处理异常，orderNo: {}", orderNo, e);
        }
    }

    private void releaseStock(String orderNo) {
        try {
            // 查询订单商品信息
            List<OrderItem> items = orderMapper.selectOrderItems(orderNo);
            if (items != null && !items.isEmpty()) {
                R result = stockFeignClient.restoreStock(items);
                if (!result.isSuccess()) {
                    log.error("库存释放失败，orderNo: {}", orderNo);
                }
            }
        } catch (Exception e) {
            log.error("释放库存异常", e);
        }
    }

    private void releaseCoupons(String orderNo) {
        try {

                R result = couponFeignClient.releaseCouponsByOrderNo(orderNo);
                if (!result.isSuccess()) {
                    log.error("优惠券释放失败，orderNo: {}", orderNo);
                }

        } catch (Exception e) {
            log.error("释放优惠券异常", e);
        }
    }
}