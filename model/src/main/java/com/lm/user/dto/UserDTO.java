package com.lm.user.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UserDTO {
    private Long id;
    private String username;

//    private String password;

    private String phone;

    private String email;


    private Integer userType;

    private LocalDateTime vipExpireTime;

}
