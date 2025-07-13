package com.lm.user.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;
    //仅作为用户名称，不再作为登录账号
    private String username;

    private String password;

    private String phone;

    private String email;

    /**
     * 用户类型：
     * 0 - 普通用户
     * 1 - VIP用户
     * 2 - 商家
     */
    private Integer userType;

    /**
     * VIP到期时间，仅在 userType = 1 时有值
     */
    private LocalDateTime vipExpireTime;

    /**
     * 商户ID，仅在 userType = 2 时有值
     */
    private Long merchantId;

    private LocalDateTime createTime;

    //删除直接挪到其他表里
//    private Integer isDeleted;
//
//    private LocalDateTime deleteTime;
}
