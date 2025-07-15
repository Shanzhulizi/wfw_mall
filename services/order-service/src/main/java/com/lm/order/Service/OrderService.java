package com.lm.order.Service;

import com.lm.common.R;
import com.lm.order.dto.OrderSubmitDTO;

public interface OrderService {
    R submitOrder(OrderSubmitDTO dto);
}
