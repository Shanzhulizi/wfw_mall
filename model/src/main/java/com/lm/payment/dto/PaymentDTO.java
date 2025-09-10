package com.lm.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class PaymentDTO {
//    private Long id;
    private String orderNo;
//    private String payNo;
//    private Long userId;  // 用户ID（关联用户服务），感觉没什么用
    private Integer payType; // 支付方式：1微信 2支付宝 3余额等
//    private BigDecimal payAmount; // 支付金额
//    private Integer status; // 0待支付 1已支付 2支付失败 3退款中 4已退款
//    private String payUrl; // 支付链接
//    // 第三方支付平台返回的原始数据（如支付宝返回的form表单）
//    private String rawResponse;
//    private LocalDateTime payTime; // 支付时间
//    private LocalDateTime createTime;
//    private LocalDateTime expireTime;

}
