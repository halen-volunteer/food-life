package com.hmdp.config;

import com.hmdp.interceptor.LoginInterceptor;
import com.hmdp.interceptor.RefreshTokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Volunteer
 * @title
 * @description
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                // 设置放行请求
                .excludePathPatterns(
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/chat",
                        "/user/code",
                        "/user/login",
                        "/blog/hot",
                        "/shop/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/voucher/**",
                        // Knife4j / OpenAPI 文档请求
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                )
                .order(1); // 优先级默认都是 0，值越大优先级越低

        // 添加刷新 token 的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .addPathPatterns("/**")
                .order(0);
    }
}
