package com.lm.order.mq.consumer;



import com.lm.common.R;
import com.lm.message.OrderCreateMessage;
import com.lm.order.Eumn.OrderStatus;
import com.lm.order.feign.*;
import com.lm.order.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
@RocketMQMessageListener(
        topic = "ORDER_CREATE_TOPIC",
        selectorExpression = "ORDER_CREATE",
        consumerGroup = "order_create_consumer_group"
)
public class OrderCreateConsumer implements RocketMQListener<OrderCreateMessage> {

    @Autowired
    private OrderMapper orderMapper;
    @Resource
    private UserFeignClient userFeign;

    @Autowired
    PaymentFeignClient paymentFeignClient;
    @Autowired
    CouponFeignClient couponFeignClient;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ProductFeignClient productFeignClient; // 商品服务Feign接口

    @Autowired
    private StockFeignClient stockFeignClient;


    @Override
    public void onMessage(OrderCreateMessage message) {
        String orderNo = message.getOrderNo();

        try {
            // 1. 幂等性检查
            if (isOrderProcessed(orderNo)) {
                log.warn("订单已处理: {}", orderNo);
                return;
            }

            // 2. 扣减库存
            R stockResult = stockFeignClient.deductStock(message.getOrderSubmitDTO().getOrderItems());
            if (!stockResult.isSuccess()) {
                handleOrderFailure(orderNo, message, "库存扣减失败");
                return; // 不抛异常，避免无限重试
            }

            // 3. 确认优惠券
            if (message.getLockedCouponIds() != null) {
                R couponResult = couponFeignClient.confirmUseCoupons(
                        message.getUserId(),
                        message.getLockedCouponIds(),
                        orderNo
                );
                if (!couponResult.isSuccess()) {
                    rollbackStock(message);
                    handleOrderFailure(orderNo, message, "优惠券确认失败");
                    return;
                }
            }

            // 4. 更新订单状态
            updateOrderStatus(orderNo, OrderStatus.WAITING_PAY.getCode());
            log.info("订单处理成功: {}", orderNo);

        } catch (Exception e) {
            // 网络/系统异常 → 可以抛出让 MQ 重试
            log.error("订单处理异常: {}", orderNo, e);
            throw new RuntimeException(e);
        }
    }


    private boolean isOrderProcessed(String orderNo) {
        Integer status = orderMapper.selectStatusByOrderNo(orderNo);
        return status != null &&
                (status == OrderStatus.WAITING_PAY.getCode() ||
                        status == OrderStatus.WAITING_PAY.getCode() ||
                        status == OrderStatus.CREATE_FAILED.getCode());
    }

    private void updateOrderStatus(String orderNo, Integer status) {
        orderMapper.updateStatus(orderNo, status);
    }

    private void rollbackStock(OrderCreateMessage message) {
        try {
            R result = stockFeignClient.restoreStock(message.getOrderSubmitDTO().getOrderItems());
            if (!result.isSuccess()) {
                log.error("库存回滚失败: {}", message.getOrderNo());
            }
        } catch (Exception e) {
            log.error("库存回滚异常", e);
        }
    }

    private void handleOrderFailure(String orderNo, OrderCreateMessage message, String errorMsg) {
        updateOrderStatus(orderNo, OrderStatus.CREATE_FAILED.getCode());
        // 释放优惠券锁定
        if (message.getLockedCouponIds() != null) {
            try {
                R result = couponFeignClient.releaseCoupons( message.getLockedCouponIds());
                if (!result.isSuccess()) {
                    log.error("优惠券释放失败: {}", orderNo);
                }
            } catch (Exception e) {
                log.error("优惠券释放异常", e);
            }
        }
    }
}