package com.lm.promotion.com.lm.promotion.mapper;

import com.lm.promotion.domain.CouponUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CouponUserMapper {
    CouponUser selectById(Long couponUserId);

    int updateStatusToUsed(Long id, Integer version);

    @Select("SELECT COUNT(*) FROM coupon_user WHERE user_id = #{userId} AND status = 'UNUSED'")
    int selectCountByUserId(Long userId);
}
