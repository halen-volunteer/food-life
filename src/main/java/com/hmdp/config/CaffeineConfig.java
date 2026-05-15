package com.hmdp.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hmdp.model.entity.Shop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置。
 */
@Configuration
public class CaffeineConfig {

    /**
     * 店铺本地缓存，只演示一处二级缓存落地。
     *
     * @return 店铺本地缓存
     */
    @Bean
    public Cache<Long, Shop> shopLocalCache() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }
}
