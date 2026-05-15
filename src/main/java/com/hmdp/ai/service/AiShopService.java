package com.hmdp.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.model.entity.Shop;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AiShopService {

    @Resource
    private ShopMapper shopMapper;

    public Shop findShop(String shopName) {
        return shopMapper.selectOne(new LambdaQueryWrapper<Shop>().eq(Shop::getName, shopName));
    }
}
