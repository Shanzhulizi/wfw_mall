package com.lm.promotion.com.lm.promotion.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper

public interface PromotionMapper {


    boolean existsCouponUser(Long userId, Long couponUserId);
}
