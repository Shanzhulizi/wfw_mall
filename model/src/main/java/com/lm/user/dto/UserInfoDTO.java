package com.lm.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoDTO {
    private Long id;
    private String username;

    private String avatarUrl;

    private Integer userType;

    private Integer couponCount;

    private LocalDateTime vipExpireTime;

}
