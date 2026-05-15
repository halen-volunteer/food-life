package com.hmdp.limiter.aop;

import com.hmdp.limiter.annotation.RateLimiter;
import com.hmdp.limiter.exception.RateLimitException;
import com.hmdp.model.dto.UserDTO;
import com.hmdp.utils.ThreadLocalUtls;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * 限流切面，基于 Redis ZSet + Lua 实现滑动窗口限流。
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    /**
     * 限流 Lua 脚本。
     */
    private static final DefaultRedisScript<Long> SLIDING_WINDOW_SCRIPT;

    static {
        SLIDING_WINDOW_SCRIPT = new DefaultRedisScript<>();
        SLIDING_WINDOW_SCRIPT.setLocation(new ClassPathResource("limiter.lua"));
        SLIDING_WINDOW_SCRIPT.setResultType(Long.class);
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 前置拦截，执行滑动窗口限流。
     *
     * @param point 切点
     * @param rateLimiter 限流注解
     */
    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        String fullKey = buildRateLimitKey(point, rateLimiter);
        Long result = executeSlidingWindowScript(fullKey, rateLimiter.window(), rateLimiter.limit());
        if (Long.valueOf(0L).equals(result)) {
            throw new RateLimitException(rateLimiter.message());
        }
        log.debug("滑动限流放行，key={}, count={}", fullKey, result);
    }

    /**
     * 执行滑动窗口限流脚本。
     *
     * @param key 限流 key
     * @param window 时间窗口（秒）
     * @param limit 限制请求数量
     * @return 当前窗口内请求数量；返回 0 表示被限流
     */
    public Long executeSlidingWindowScript(String key, int window, int limit) {
        long now = System.currentTimeMillis();
        return stringRedisTemplate.execute(
                SLIDING_WINDOW_SCRIPT,
                Collections.singletonList(key),
                String.valueOf(window),
                String.valueOf(limit),
                String.valueOf(now)
        );
    }

    /**
     * 构建限流 key。
     *
     * @param point 切点
     * @param rateLimiter 限流注解
     * @return 限流 key
     */
    private String buildRateLimitKey(JoinPoint point, RateLimiter rateLimiter) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        StringBuilder keyBuilder = new StringBuilder(rateLimiter.key())
                .append(method.getDeclaringClass().getName())
                .append(":")
                .append(method.getName());

        switch (rateLimiter.type()) {
            case IP:
                keyBuilder.append(":ip:").append(getClientIp());
                break;
            case USER:
                keyBuilder.append(":user:").append(getCurrentUserId());
                break;
            case METHOD:
            default:
                break;
        }
        return keyBuilder.toString();
    }

    /**
     * 获取客户端 IP。
     *
     * @return 客户端 IP
     */
    private String getClientIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return "unknown";
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip == null || ip.isBlank() ? "unknown" : ip;
    }

    /**
     * 获取当前用户 ID。
     *
     * @return 用户 ID；未登录时返回 anonymous
     */
    private String getCurrentUserId() {
        UserDTO user = ThreadLocalUtls.getUser();
        if (user == null || user.getId() == null) {
            return "anonymous";
        }
        return String.valueOf(user.getId());
    }
}
