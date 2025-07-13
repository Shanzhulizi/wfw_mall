package com.lm.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserUpdateDTO {

    private Long id;
    private String username;

    private String password;

    private String phone;

    private String email;

    private LocalDateTime vipExpireTime;

    private Integer userType;

    private Long merchantId;

    private Integer isDeleted;

    private LocalDateTime deleteTime;
}

