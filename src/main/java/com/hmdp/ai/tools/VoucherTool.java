package com.hmdp.ai.tools;

import com.hmdp.ai.service.AiVoucherService;
import com.hmdp.model.entity.Voucher;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VoucherTool {

    @Resource
    private AiVoucherService aiVoucherService;

    @Tool("根据商家名称查询商家的优惠券信息")
    public List<Voucher> findVoucherByShopName(@P("商家名称") String shopName) {
        return aiVoucherService.findVoucherByShopName(shopName);
    }

    @Tool("根据用户手机号查询用户拥有的优惠券信息")
    public List<Voucher> findVoucherByUserPhone(@P("用户手机号") String userPhone) {
        return aiVoucherService.findVoucherByUserPhone(userPhone);
    }
}
