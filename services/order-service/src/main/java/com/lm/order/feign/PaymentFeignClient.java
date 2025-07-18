package com.lm.order.feign;

import com.lm.order.feign.fallback.PaymentFeignClientFallback;
import com.lm.order.feign.fallback.ProductFeignClientFallback;
import com.lm.payment.dto.PaymentDTO;
import com.lm.payment.dto.PaymentInfoDTO;
import com.lm.payment.dto.PaymentResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "payment-service", fallback = PaymentFeignClientFallback.class) // feign客户端
public interface PaymentFeignClient {

    @PostMapping("/payment/create")
    PaymentInfoDTO createPayment(PaymentInfoDTO paymentInfoDTO);
}
