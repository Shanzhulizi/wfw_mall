package com.lm.user.dto;

import lombok.Data;

@Data
public class UserLoginDTO {

    private String phone;
    private String password;
    private String code;

}
