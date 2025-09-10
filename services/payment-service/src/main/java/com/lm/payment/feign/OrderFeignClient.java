package com.lm.payment.feign;


import com.lm.order.dto.ReceiverInfoDTO;
import com.lm.order.vo.OrderVO;
import com.lm.payment.feign.fallback.OrderFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "order-service", fallback = OrderFeignClientFallback.class) // feign客户端
public interface OrderFeignClient {

    @GetMapping("/order/detail")
    OrderVO getOrder(@RequestParam("orderNo")  String orderNo);

    @PostMapping("/order/updatePaid")
    void updateOrderPaid(@RequestParam("orderNo") String orderNo);
}

