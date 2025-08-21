package com.lm.user.mapper;

import com.lm.user.domain.UserDeleteLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDeleteMapper {

    int insertDeleteRecord(UserDeleteLog userDeleteLog);
}
