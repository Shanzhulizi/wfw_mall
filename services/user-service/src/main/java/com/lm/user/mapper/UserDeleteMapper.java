package com.lm.user.mapper;

import com.lm.user.domain.UserDeleteLog;

import java.time.LocalDateTime;

public interface UserDeleteMapper {

    int insertDeleteRecord(UserDeleteLog userDeleteLog);
}
