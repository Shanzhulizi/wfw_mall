package com.lm.user.dto;

import lombok.Data;

/**
 * 这里本来是有用的，但是我写用户登录这个部分很混乱，所以我不敢乱改
 */
@Data
public class UserFilgerDTO {
    private Long id;
    private String phone;
    private Integer userType;

}
