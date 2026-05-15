package com.hmdp.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Volunteer
 * @title
 * @description
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;

    @Value("${spring.data.redis.password:}")
    private String password;

    /**
     * 创建 Redisson 配置对象，并交给 IOC 管理
     *
     * @return RedissonClient
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        if (password != null && !password.isBlank()) {
            config.useSingleServer().setPassword(password);
        }
        return Redisson.create(config);
    }
}
