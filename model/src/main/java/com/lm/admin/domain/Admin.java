package com.lm.admin.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Admin {

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
