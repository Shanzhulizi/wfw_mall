package com.lm.gateway.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsGlobalConfiguration {
    //TODO 这里是没有用的，因为跨域是浏览器的事情，但是我没有写前端，所以我不知道跨域有没有效果
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        // 允许哪些请求源访问
////        config.addAllowedOrigin("*"); // 也可以配置具体域名如：http://localhost:3000
//        // 设置允许哪些源跨域（80 端口）
//        config.addAllowedOrigin("http://your-domain.com"); // 线上用域名
//        config.addAllowedOrigin("http://localhost"); // 本地用
//        config.addAllowedOrigin("http://127.0.0.1"); // 补充 IP
//        config.addAllowedOrigin("http://localhost:80"); // 精确限制端口
//        // 是否发送 Cookie
//        config.setAllowCredentials(true);
//        // 允许哪些 HTTP 方法访问
//        config.addAllowedMethod("*");
//        // 允许哪些头访问
//        config.addAllowedHeader("*");
//        // 暴露哪些头
//        config.addExposedHeader("*");
//
//        // 匹配所有路径
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsWebFilter(source);
//    }
}
