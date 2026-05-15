package com.hmdp.controller;


import com.hmdp.limiter.annotation.RateLimiter;
import com.hmdp.model.dto.Result;
import com.hmdp.service.IVoucherOrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Volunteer
 *
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {
    @Resource
    private IVoucherOrderService voucherOrderService;

    /**
     * 抢购秒杀券
     *
     * @param voucherId
     * @return
     */
    @PostMapping("seckill/{id}")
    @RateLimiter(
            key = "voucher-order:seckill:",
            window = 10,
            limit = 5,
            message = "秒杀活动太火爆，请稍后再试",
            type = RateLimiter.LimitType.METHOD
    )
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
