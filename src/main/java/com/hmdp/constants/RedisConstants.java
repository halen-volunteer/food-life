package com.hmdp.constants;

public class RedisConstants {
    private RedisConstants() {
    }

    // 登录相关
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 200L;
    public static final String LOGIN_USER_KEY = "login:user:";
    public static final Long LOGIN_USER_TTL = 3600L;

    // 店铺缓存相关
    public static final Long CACHE_NULL_TTL = 10L;
    public static final Long CACHE_SHOP_TTL = 30L;
    public static final String CACHE_SHOP_KEY = "cache:shop:";
    public static final String CACHE_SHOP_TYPE_KEY = "cache:type:";
    public static final Long CACHE_SHOP_TYPE_TTL = 30L;
    public static final Long CACHE_SHOP_LOGICAL_TTL = 10L;

    // 订单相关
    public static final String ID_PREFIX = "icr:";
    public static final String SECKILL_VOUCHER_ORDER = "order";

    // 秒杀相关
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String SECKILL_ORDER_KEY = "seckill:order:";

    // 锁相关
    public static final String LOCK_ORDER_KEY = "lock:order:";
    public static final String LOCK_SHOP_KEY = "lock:shop:";

    // Redis Stream 旧常量，保留仅为兼容已有资源文件和文档引用
    public static final String QUEUE_NAME = "stream.orders";
    public static final String GROUP_NAME = "g1";
    public static final String OFF_SET = "0";

    // 博客及社交相关
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FOLLOW_KEY = "follows:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "shop:geo:";
    public static final String USER_SIGN_KEY = "sign:";
}
