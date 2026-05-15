package com.hmdp.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hmdp.constants.RedisConstants;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.model.entity.SeckillVoucher;
import com.hmdp.model.entity.VoucherOrder;
import com.hmdp.service.ISeckillVoucherService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 未支付订单自动关闭定时任务
 */
@Slf4j
@Component
public class VoucherOrderTask {

    /**
     * 订单超时时间，单位：分钟
     */
    private static final int EXPIRE_MINUTES = 15;

    /**
     * 每次扫描的最大订单数
     */
    private static final int BATCH_SIZE = 200;

    @Resource
    private VoucherOrderMapper voucherOrderMapper;

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 每分钟扫描一次超时未支付订单
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeExpiredVoucherOrders() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(EXPIRE_MINUTES);
        List<VoucherOrder> orders = voucherOrderMapper.selectList(new LambdaQueryWrapper<VoucherOrder>()
                .eq(VoucherOrder::getStatus, 1)
                .lt(VoucherOrder::getCreateTime, expireTime)
                .orderByAsc(VoucherOrder::getCreateTime)
                .last("limit " + BATCH_SIZE));

        if (orders == null || orders.isEmpty()) {
            return;
        }

        for (VoucherOrder order : orders) {
            try {
                closeSingleOrder(order);
            } catch (Exception e) {
                log.error("关闭超时订单失败，orderId={}", order.getId(), e);
            }
        }
    }

    /**
     * 关闭单个超时订单
     * 使用状态条件更新作为乐观锁，避免和支付操作并发冲突
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeSingleOrder(VoucherOrder order) {
        int updated = voucherOrderMapper.update(null, new LambdaUpdateWrapper<VoucherOrder>()
                .eq(VoucherOrder::getId, order.getId())
                .eq(VoucherOrder::getStatus, 1)
                .set(VoucherOrder::getStatus, 4));
        if (updated <= 0) {
            return;
        }

        boolean success = seckillVoucherService.update(new LambdaUpdateWrapper<SeckillVoucher>()
                .eq(SeckillVoucher::getVoucherId, order.getVoucherId())
                .setSql("stock = stock + 1"));
        if (!success) {
            throw new RuntimeException("释放秒杀库存失败");
        }

        String orderKey = RedisConstants.SECKILL_ORDER_KEY + order.getVoucherId();
        stringRedisTemplate.opsForSet().remove(orderKey, order.getUserId().toString());
    }
}
