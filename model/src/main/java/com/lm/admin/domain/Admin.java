package com.lm.admin.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Admin {

        private Long id;

        private String username;

        private String password;

        /**
         * 权限（固定为 1，超级管理员）
         */
        private Integer permission;

        /**
         * 账号状态：1 正常，0 禁用
         */
        private Integer status;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;
}
