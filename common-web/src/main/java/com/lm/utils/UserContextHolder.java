package com.lm.utils;

import com.lm.user.dto.UserFilgerDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户上下文持有者，用于获取当前请求的用户信息
 * 通过请求头中的 x-user-id, x-user-phone, x-user-type 获取用户信息
 */

public class UserContextHolder {

    public static UserFilgerDTO getUser() {

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        String idStr = request.getHeader("x-user-id");
        if (idStr == null || idStr.isEmpty()) {
            // 这里可以选择抛异常或者返回null
//            throw new RuntimeException("请求头缺少 x-user-id");
            // 不抛异常，返回 null 表示无登录用户
            return new UserFilgerDTO();
        }
        UserFilgerDTO user = new UserFilgerDTO();
        user.setId(Long.valueOf(request.getHeader("x-user-id")));
        user.setPhone(request.getHeader("x-user-phone"));
        user.setUserType(Integer.valueOf(request.getHeader("x-user-type")));

        return user;
    }
}