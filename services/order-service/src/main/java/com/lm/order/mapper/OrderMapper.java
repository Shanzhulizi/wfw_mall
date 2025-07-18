package com.lm.order.mapper;

import com.lm.order.domain.Order;
import com.lm.order.domain.OrderItem;
import com.lm.order.domain.OrderShipping;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {


    void insertOrder(Order order);

    void insertOrderShipping(OrderShipping orderShipping);


    void insertOrderItems(List<OrderItem> items);
}
