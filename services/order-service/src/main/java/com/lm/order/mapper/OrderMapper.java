package com.lm.order.mapper;

import com.lm.order.domain.Order;
import com.lm.order.domain.OrderItem;
import com.lm.order.domain.OrderShipping;
import com.lm.order.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
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


    OrderVO getOrderDetail(String orderNo);

    @Update("update `order` set status = 5, pay_time = #{now} where order_no = #{orderNo}")
    void updateOrderStatusToPaid(String orderNo, LocalDateTime now);
}
