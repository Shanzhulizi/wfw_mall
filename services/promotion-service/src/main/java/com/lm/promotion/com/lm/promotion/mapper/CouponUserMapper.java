package com.lm.promotion.com.lm.promotion.mapper;

import com.lm.promotion.domain.CouponUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CouponUserMapper {

    // 查询用户指定的券
    @Select({
            "<script>",
            "SELECT id, coupon_id AS couponId, user_id AS userId, status, order_no AS orderNo,",
            "lock_time AS lockTime, use_order_id AS useOrderId, use_time AS useTime,",
            "expire_time AS expireTime, created_at AS createdAt, updated_at AS updatedAt ",
            "FROM coupon_user ",
            "WHERE user_id = #{userId} ",
            "AND id IN ",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    List<CouponUser> findByIdsAndUser(@Param("ids") List<Long> ids,
                                      @Param("userId") Long userId);

    // 锁券：status=0 才能更新为锁定状态，并写入订单ID
    @Update("UPDATE coupon_user " +
            "SET status = 1,  lock_time = NOW(), updated_at = NOW() " +
            "WHERE id = #{id} AND user_id = #{userId} AND status = 0")
    int lockCoupon(@Param("id") Long id,
                   @Param("userId") Long userId
    );

    @Update("UPDATE coupon_user " +
            "SET status = 2, order_no = #{orderNo} " +  // 已使用
            "WHERE id = #{id} AND user_id = #{userId} AND status = 1")
    int confirmCoupon(Long couponUserId, Long userId, String orderNo);


    // 释放优惠券（订单失败/超时取消）：status 1 或 2 -> 0
    @Update("UPDATE coupon_user " +
            "SET status = 0, order_no = NULL " +
            "WHERE id = #{id}  AND status IN (1, 2)")
    int releaseCoupon(@Param("id") Long id                    );

    @Update("UPDATE coupon_user " +
            "SET status = 0, order_no = NULL " +
            "WHERE id = #{couponUserId} AND status IN (1, 2)")
    int cancelLockCoupon(Long couponUserId);

    @Update("UPDATE coupon_user " +
            "SET status = 0, order_no = NULL " +
            "WHERE order_no = #{orderNo} AND status IN (1, 2)")
    int releaseCouponByOrderNo(String orderNo);
}

