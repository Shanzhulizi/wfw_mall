package com.lm.promotion.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LockCouponsDTO {
    private List<Long> couponUserIds;  // 用户选择的优惠券
    private Long userId;               // 当前用户ID（确保是自己）
    private Long merchantId;           // 商家ID
    private BigDecimal orderAmount;    // 订单金额
}
