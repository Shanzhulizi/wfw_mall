package com.lm.payment.feign.fallback;

import com.lm.order.dto.ReceiverInfoDTO;
import com.lm.order.vo.OrderVO;
import com.lm.payment.feign.OrderFeignClient;

public class OrderFeignClientFallback implements OrderFeignClient {


    @Override
    public OrderVO getOrder(String orderNo) {
        return null;
    }

    @Override
    public void updateOrderPaid(String orderNo) {

    }
}
