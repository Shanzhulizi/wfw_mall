package com.lm.user.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantApplication {
    private Long id;
    private String merchantName;
    private String contactPhone;
    private String password;          // 商家登录密码
    private String contactEmail;
    private Long merchantId;        // 审核通过后生成商家ID
    private Integer applicationStatus; // 0 待审核, 1 通过, 2 拒绝
    private String reason;          // 拒绝原因或备注
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}