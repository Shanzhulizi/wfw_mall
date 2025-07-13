package com.lm.user.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.security.Key;

public class JwtUtil {

    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24; // 1天
    private static final String SECRET_KEY = "lm-wfw-mall-secret-key-1234567890987654321"; // 长度 >= 32 字符

    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 生成 Token
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    // 解析 Token
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}