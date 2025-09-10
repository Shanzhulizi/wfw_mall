package com.lm.promotion.com.lm.promotion.mapper;

import com.lm.promotion.domain.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper

public interface CouponMapper {


    @Select("SELECT id, merchant_id AS merchantId, name, type, discount_amount AS discountAmount, " +
            "discount_rate AS discountRate, min_amount AS minAmount, valid_start AS validStart, valid_end AS validEnd " +
            "FROM coupon WHERE id = #{couponId}")
    Coupon findById(Long couponId);
}
