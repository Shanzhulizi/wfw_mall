package com.lm.utils;

import com.lm.admin.dto.AdminFilterDTO;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class AdminContextHolder {

    public static AdminFilterDTO getAdmin() {

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        String idStr = request.getHeader("x-admin-id");
        if (idStr == null || idStr.isEmpty()) {
            // 这里可以选择抛异常或者返回null
            throw new RuntimeException("请求头缺少 x-user-id");
        }
        if (request.getHeader("x-admin-permission") == null) {
            throw new RuntimeException("请求头缺少 x-admin-permission");
        }

        AdminFilterDTO admin = new AdminFilterDTO();
        admin.setAdminId(Long.valueOf(request.getHeader("x-admin-id")));
        admin.setPermission(Integer.valueOf(request.getHeader("x-admin-permission")));

        return admin;
    }
}
