package com.lm.user.mapper;


import com.lm.user.domain.User;
import com.lm.user.dto.UserDTO;
import com.lm.user.dto.UserUpdateDTO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {


    User selectByUsername(String username);

    /**
     * 通过手机验证码验证后建立账号
     * @param phone
     */
    int insertAccountWithPhone(String phone, LocalDateTime createTime);

    User selectByPhone(String phone);


    int updateUserByDTO(UserUpdateDTO user);

    int deleteUserById(Long userId);
}
