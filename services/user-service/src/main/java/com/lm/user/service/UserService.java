package com.lm.user.service;

import com.lm.common.R;
import com.lm.user.domain.User;
import com.lm.user.dto.RegisterDTO;

public interface UserService {


    User login(String username, String password);



  R changeAccountInfo(String username, String password, String email, Long userId);


    R createAccountOrLoginWithPhone(String phone);
}
