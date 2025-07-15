package com.lm.order.domain;

import com.lm.user.domain.ReceiverInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    //订单编号（例如：20250705123456xxxx）
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private BigDecimal totalAmount;
    //实付金额（含优惠）
    private BigDecimal payAmount;
    private int payType; // 支付方式：1微信 2支付宝 3余额等
    private LocalDateTime payTime;
    private int status; // 0待付款 1已付款 2已发货 3已收货 4已关闭 5退款中 6已退款
    private int orderType; // 0普通订单 1秒杀 2团购等
    private String remark;  // 用户备注
    private String cancelReason;
    private LocalDateTime cancelTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Long receiverInfoId;
}
