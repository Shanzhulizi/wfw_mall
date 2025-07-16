package com.lm.order.service;

import com.lm.common.R;
import com.lm.order.dto.OrderSubmitDTO;

public interface OrderService {
    R submitOrder(OrderSubmitDTO dto);
}
