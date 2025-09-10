package com.lm.order.feign;


import com.lm.common.R;
import com.lm.order.domain.OrderItem;
import com.lm.order.feign.fallback.ProductFeignClientFallback;
import com.lm.order.feign.fallback.StockFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "stock-service",fallback = StockFeignClientFallback.class) // feign客户端

public interface StockFeignClient {

    /**
     * 扣减库存（订单创建时调用）
     */
    @PostMapping("/stock/deduct")
    R deductStock(@RequestBody List<OrderItem> orderItems);

    /**
     * 回滚库存（订单失败/取消时调用）
     */
    @PostMapping("/restore")
    R restoreStock(@RequestBody List<OrderItem> orderItems);
}
