package com.lm.user.mapper;


import com.lm.order.dto.ReceiverInfoDTO;
import com.lm.user.domain.User;
import com.lm.user.dto.UserInfoDTO;
import com.lm.user.dto.UserUpdateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 通过手机验证码验证后建立账号
     * @param phone
     */
    int insertAccountWithPhone(String phone, LocalDateTime createTime);

    User selectByPhone(String phone);


    int updateUserByDTO(UserUpdateDTO user);

    int deleteUserById(Long userId);


    @Select("SELECT * FROM user WHERE id = #{userId}")
    User getById(Long userId);

    UserInfoDTO getUserInfoById(Long userId);

    List<ReceiverInfoDTO> getReceiverInfoByUserId(Long userId);
}
