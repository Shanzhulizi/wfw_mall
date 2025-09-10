package com.lm.order.feign.fallback;

import com.lm.common.R;
import com.lm.order.domain.OrderItem;
import com.lm.order.feign.StockFeignClient;

import java.util.List;

public class StockFeignClientFallback implements StockFeignClient {


    @Override
    public R deductStock(List<OrderItem> orderItems) {
        return null;
    }

    @Override
    public R restoreStock(List<OrderItem> orderItems) {
        return null;
    }
}
