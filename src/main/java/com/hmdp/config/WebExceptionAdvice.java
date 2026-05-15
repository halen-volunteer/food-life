package com.hmdp.config;

import com.hmdp.limiter.exception.RateLimitException;
import com.hmdp.model.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {

    /**
     * 限流异常单独处理，直接把友好提示返回给前端。
     *
     * @param e 限流异常
     * @return 通用返回体
     */
    @ExceptionHandler(RateLimitException.class)
    public Result handleRateLimitException(RateLimitException e) {
        log.warn("触发滑动限流: {}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 全局异常处理捕获，记录通用日志，返回给前端通用响应。
     *
     * @param e 运行时异常
     * @return 通用返回体
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return Result.fail("服务器异常:" + e.getMessage());
    }
}
