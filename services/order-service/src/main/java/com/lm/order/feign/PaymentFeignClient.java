package com.lm.order.feign;

import com.lm.order.feign.fallback.PaymentFeignClientFallback;
import com.lm.payment.dto.PaymentInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "payment-service", fallback = PaymentFeignClientFallback.class) // feign客户端
public interface PaymentFeignClient {

    @PostMapping("/payment/create")
    PaymentInfoDTO createPayment(PaymentInfoDTO paymentInfoDTO);
}
