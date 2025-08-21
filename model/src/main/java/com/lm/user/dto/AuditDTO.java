package com.lm.user.dto;

import lombok.Data;

@Data
public class AuditDTO {
    private Long id;
    private Integer status;
    private String reason;
}