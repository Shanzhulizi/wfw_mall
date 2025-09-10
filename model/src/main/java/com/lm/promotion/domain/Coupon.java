package com.lm.promotion.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private Long id;
    private Long merchantId;
    private String name;
    private Integer type; // 1=满减 2=折扣 3=现金
    private BigDecimal discountAmount; // 满减/现金券金额
    private BigDecimal discountRate;   // 折扣比例 (0.9 = 9折)
    private BigDecimal minAmount;      // 使用门槛
    private LocalDateTime validStart;
    private LocalDateTime validEnd;



}
