package com.lm.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentInfoDTO {
    private Long id;
    private String orderNo;
    private String payNo;
    private BigDecimal payAmount; // 支付金额
    private Integer status; // 0待支付 1已支付 2支付失败 3退款中 4已退款
    private String payUrl; // 支付链接
    private LocalDateTime createTime;
    private LocalDateTime expireTime;

}
