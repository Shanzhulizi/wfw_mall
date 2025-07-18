package com.lm.order.dto;

import com.lm.order.domain.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderSubmitDTO {
    Long merchantId;
    BigDecimal totalAmount;
    //实付金额（含优惠）
    BigDecimal payAmount;
    Integer orderType; // 0普通订单 1秒杀 2团购等
    String remark;  // 用户备注
    //淘宝的提交订单是需要先选好支付的方式的
    private int payType; // 支付方式：1微信 2支付宝 3余额等
    List<OrderItem> orderItems; // 订单项列表
    Long receiverInfoId; // 收货信息ID

    private List<Long> couponUserIds;  // 多张优惠券
    private Long fullDiscountId; // 满减活动ID
}
