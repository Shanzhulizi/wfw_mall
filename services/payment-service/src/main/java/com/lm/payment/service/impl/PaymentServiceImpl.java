package com.lm.payment.service.impl;


import com.lm.order.Eumn.OrderStatus;
import com.lm.order.vo.OrderVO;
import com.lm.payment.feign.OrderFeignClient;
import com.lm.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderFeignClient orderFeignClient; // 通过 Feign 调用订单服务，修改订单状态


    @Override
    public boolean payOrder(String orderNo, Integer payType) {
        // 1. 校验订单是否存在、是否待支付
        OrderVO order = orderFeignClient.getOrder(orderNo);
        if (order == null || order.getStatus() != OrderStatus.WAITING_PAY.getCode()) {
            throw new RuntimeException("订单不存在或状态异常");
        }

        // 2. 模拟调用第三方支付
        // TODO: 实际对接支付宝/微信 SDK
        System.out.println("模拟调用 " + payType + " 支付订单：" + orderNo);

        // 3. 支付成功后回调订单服务，修改订单状态
        orderFeignClient.updateOrderPaid(orderNo);

        return true;
    }


}
