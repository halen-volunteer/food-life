package com.hmdp.limiter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 滑动窗口限流注解。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流 key 前缀。
     */
    String key() default "rate_limit:";

    /**
     * 时间窗口大小，单位：秒。
     */
    int window() default 10;

    /**
     * 时间窗口内允许的请求数。
     */
    int limit() default 20;

    /**
     * 限流提示信息。
     */
    String message() default "系统繁忙，请稍后再试";

    /**
     * 限流维度，默认按方法限流。
     */
    LimitType type() default LimitType.METHOD;

    enum LimitType {
        /**
         * 按调用方 IP 限流。
         */
        IP,
        /**
         * 按用户 ID 限流。
         */
        USER,
        /**
         * 按方法限流。
         */
        METHOD
    }
}
