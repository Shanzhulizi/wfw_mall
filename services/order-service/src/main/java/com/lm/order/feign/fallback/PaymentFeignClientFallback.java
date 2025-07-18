package com.lm.order.feign.fallback;

import com.lm.order.feign.PaymentFeignClient;
import com.lm.payment.dto.PaymentDTO;
import com.lm.payment.dto.PaymentInfoDTO;
import com.lm.payment.dto.PaymentResultDTO;

public class PaymentFeignClientFallback implements PaymentFeignClient {


    @Override
    public PaymentInfoDTO createPayment(PaymentInfoDTO paymentInfoDTO) {

        return paymentInfoDTO; // 返回一个空的支付结果或默认值
    }
}