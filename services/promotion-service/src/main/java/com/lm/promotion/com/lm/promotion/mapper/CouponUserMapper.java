package com.lm.promotion.com.lm.promotion.mapper;

import com.lm.promotion.domain.CouponUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponUserMapper {
    CouponUser selectById(Long couponUserId);

    int updateStatusToUsed(Long id, Integer version);
}
