package com.lm.order.mapper;

import com.lm.order.domain.Order;
import com.lm.order.domain.OrderItem;
import com.lm.order.domain.OrderShipping;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {




    void insertOrder(Order order);


    void insertOrderItem(OrderItem items);


    void insertOrderShipping(OrderShipping orderShipping);



    /**
     * 更新订单状态
     */
    int updateStatus(String orderNo, int code);

    /**
     * 查询订单状态
     */
    Integer selectStatusByOrderNo(String orderNo);

    
    
    List<OrderItem> selectOrderItems(String orderNo);


}
