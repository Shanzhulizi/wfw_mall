package com.lm.payment.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    Long id;
    String OrderNo;
    String PayNo;
    Long UserId;
    Integer payType; // 支付方式：1微信 2支付宝 3余额等
    BigDecimal payAmount; // 支付金额
    Integer status; // 0待支付 1已支付 2支付失败 3退款中 4已退款
    LocalDateTime payTime; // 支付时间
    LocalDateTime createTime; // 创建时间
    LocalDateTime updateTime; // 更新时间

}
