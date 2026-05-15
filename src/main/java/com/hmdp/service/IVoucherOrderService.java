package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.model.dto.Result;
import com.hmdp.model.entity.VoucherOrder;

/**
 * <p>
 * 秒杀订单服务类
 * </p>
 *
 * @author Volunteer
 *
 */
public interface IVoucherOrderService extends IService<VoucherOrder> {

    /**
     * 抢购秒杀券
     *
     * @param voucherId 优惠券 id
     * @return 下单结果
     */
    Result seckillVoucher(Long voucherId);

    /**
     * 创建订单
     *
     * @param voucherOrder 订单信息
     */
    void createVoucherOrder(VoucherOrder voucherOrder);

    /**
     * 真正执行库存扣减和订单创建
     *
     * @param voucherOrder 订单信息
     */
    void doCreateVoucherOrder(VoucherOrder voucherOrder);
}
