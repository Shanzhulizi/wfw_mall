package com.lm.stock.service;

import com.lm.common.R;
import com.lm.order.domain.OrderItem;

import java.util.List;

public interface StockService {
    
    void deductStock(List<OrderItem> orderItems);

    void restoreStock(List<OrderItem> orderItems);
}
