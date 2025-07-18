package com.lm.promotion.dto;

import lombok.Data;

import java.util.List;

@Data
public class LockCouponsDTO {
    private List<Long> couponUserIds
            ;
    private  Long userId;
}
