package com.lm.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponUseDTO {
    private Long couponId;
    private String couponType; // 满减 / 折扣 / 代金券
    private BigDecimal discountAmount;
    private String scope; // 作用范围：全场/单品/分类
    Long AppliedSkuId;  // 该优惠只对skuId为10002的商品生效
}
