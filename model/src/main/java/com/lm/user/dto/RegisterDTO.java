package com.lm.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterDTO {
    private String username;

    private String password;

    private String phone;

    private String email;


    private Integer isDeleted;

}
