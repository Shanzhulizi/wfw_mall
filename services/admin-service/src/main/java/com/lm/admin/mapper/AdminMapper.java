package com.lm.admin.mapper;

import com.lm.admin.domain.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    Admin selectByUsername(String username);








}
