package com.lm.user.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUser {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    /**
     * 是否超级管理员（固定为 1）
     */
    private Integer isSuper;

    /**
     * 账号状态：1 正常，0 禁用
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}