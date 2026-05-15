package com.hmdp.ai.tools;

import com.hmdp.ai.service.AiShopService;
import com.hmdp.model.entity.Shop;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class ShopTool {

    @Resource
    private AiShopService aiShopService;

    @Tool("根据商家名称查询商家信息")
    public Shop findShop(@P("商家名称") String shopName) {
        return aiShopService.findShop(shopName);
    }
}
