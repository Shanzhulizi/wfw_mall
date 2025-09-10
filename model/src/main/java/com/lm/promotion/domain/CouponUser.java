package com.lm.promotion.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponUser {

    private Long id;
    private Long couponId; // 优惠券ID
    private Long userId; // 用户ID
    private Integer status; // 状态：0-未使用，1-已使用，2-已过期
    private String orderNo;

    private LocalDateTime lockTime;
    private Long useOrderId;
    private LocalDateTime useTime;
    private LocalDateTime expireTime;

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 创建时间


}
