package com.lm.gateway.filter;

import com.lm.common.utils.JwtUtils;
import com.lm.gateway.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();
        // 1. 放行登录和注册请求
        if (path.contains("/login") || path.contains("/register")) {
            return chain.filter(exchange); // 放行，不做 token 校验
        }


        String token = request.getHeaders().getFirst(TOKEN_HEADER);

//        log.info("Received token: {}", token);
        if (token == null || !token.startsWith(BEARER_PREFIX)) {
            log.warn("未携带token 或者token不是以Bearer开头");
            // 如果没有携带token，直接返回401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();

        }

        try {
            Claims claims = JwtUtils.parseToken(token.replace(BEARER_PREFIX, ""));

            String role = claims.get("role").toString();
            if (role == null || role.isEmpty() || !role.matches("^(admin|user)$")) {
                log.warn("Token does not contain role information");
                throw new Exception();
            }


            if ("admin".equals(role)) {
                log.info("Admin role detected, processing as admin");
                String adminId = claims.get("id").toString();
                String permission = claims.get("permission").toString();
                log.info("Admin ID: {}, Permission: {}", adminId, permission);
                // 添加管理员信息到请求头，转发给下游服务
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("x-admin-id", adminId)
                        .header("x-admin-permission", permission)
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } else {
                log.info("User role detected, processing as user");
                String userId = claims.get("id").toString();
                String phone = claims.get("phone").toString();
                String userType = claims.get("userType").toString();

                log.info("User ID: {}, Phone: {}, User Type: {}", userId, phone, userType);

                // 添加用户信息到请求头，转发给下游服务
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("x-user-id", userId)
                        .header("x-user-phone", phone)
                        .header("x-user-type", userType)
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
        } catch (ExpiredJwtException e) {
            // token过期，返回401
            log.warn("Token expired: {}", e.getMessage());
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        } catch (Exception e) {
            // token非法，返回401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

    }

    @Override
    public int getOrder() {
        return -100; // 优先执行
    }
}
