package com.lm.user.utils;

import java.util.Random;

public class VertifyCodeUtil {
    public String sendVerificationCode(String phone) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // [100000, 999999]

        return String.valueOf(code);

    }
}
