package com.lm.order.dto;

import com.lm.order.domain.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderSubmitDTO {
    //订单编号（例如：20250705123456xxxx）
//    String orderNo;
//    Long userId;
    Long merchantId;
    BigDecimal totalAmount;
    //实付金额（含优惠）
    BigDecimal payAmount;
//    Integer status; // 0待付款 1已付款 2已发货 3已收货 4已关闭 5退款中 6已退款
    Integer orderType; // 0普通订单 1秒杀 2团购等
    String remark;  // 用户备注

//    LocalDateTime createTime;

    List<OrderItem> orderItems; // 订单项列表

    Long receiverInfoId; // 收货信息ID
}
