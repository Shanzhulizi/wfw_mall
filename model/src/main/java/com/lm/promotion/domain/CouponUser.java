package com.lm.promotion.domain;

import lombok.Data;

@Data
public class CouponUser {

    private Long id;
    private Long couponId; // 优惠券ID
    private Long userId; // 用户ID
    private Integer status; // 状态：0-未使用，1-已使用，2-已过期
    private Long usedTime; // 过期时间
    private String couponCode; // 优惠券编码
    private Long createTime; // 创建时间
    private Integer version; //作为乐观锁

}
