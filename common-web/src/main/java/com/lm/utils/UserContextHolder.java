package com.lm.utils;

import com.lm.user.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UserContextHolder {
    public static UserDTO getUser() {

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        String idStr = request.getHeader("x-user-id");
        if (idStr == null || idStr.isEmpty()) {
            // 这里可以选择抛异常或者返回null
            throw new RuntimeException("请求头缺少 x-user-id");
        }
        UserDTO user = new UserDTO();
        user.setId(Long.valueOf(request.getHeader("x-user-id")));
        user.setPhone(request.getHeader("x-user-phone"));
        user.setUserType(Integer.valueOf(request.getHeader("x-user-type")));

        return user;
    }
}