package com.lm.user.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDeleteLog {
    private Long userId;
    private String deleteReason;
    private String ip;
    private String userAgent;
    private String phone;
    private LocalDateTime deleteTime;
}
