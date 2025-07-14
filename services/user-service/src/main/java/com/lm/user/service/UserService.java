package com.lm.user.service;

import com.lm.common.R;
import com.lm.user.domain.User;

public interface UserService {


    R changeAccountInfo(String username, String password, String email, Long userId);


    R createAccountOrLoginWithPhone(String phone);


    void deleteUser(Long userId, String deleteReason, String ip, String userAgent, String phone, int userType);

    R loginWithPasswordOrCode(String phone, String password, String code);
}
