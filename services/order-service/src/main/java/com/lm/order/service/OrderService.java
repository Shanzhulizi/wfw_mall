package com.lm.order.service;

import com.lm.order.dto.OrderSubmitDTO;
import com.lm.order.dto.OrderSubmitTestDTO;
import com.lm.order.vo.OrderVO;

public interface OrderService {
    OrderVO submitOrder(OrderSubmitDTO dto);

    OrderVO submitOrderTest(OrderSubmitTestDTO dto);
}
