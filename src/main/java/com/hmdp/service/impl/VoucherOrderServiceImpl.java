package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.config.RabbitMqConfig;
import com.hmdp.constants.RedisConstants;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.model.dto.Result;
import com.hmdp.model.dto.SeckillOrderMessage;
import com.hmdp.model.entity.SeckillVoucher;
import com.hmdp.model.entity.VoucherOrder;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.ThreadLocalUtls;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.hmdp.constants.RedisConstants.SECKILL_VOUCHER_ORDER;

/**
 * <p>
 * 秒杀订单服务实现类
 * </p>
 *
 * @author Volunteer
 *
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    /**
     * 加载 Lua 脚本：
     * 用于在 Redis 中原子判断库存是否充足，以及用户是否已经下单
     */
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 抢购秒杀券：
     * 1. 执行 Lua 脚本做资格校验
     * 2. 校验通过后发送 RabbitMQ 消息
     * 3. 由消费者异步扣减数据库库存并创建订单
     *
     * @param voucherId 优惠券 id
     * @return 下单结果
     */
    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId = ThreadLocalUtls.getUser().getId();
        long orderId = redisIdWorker.nextId(SECKILL_VOUCHER_ORDER);

        // 1. 执行 Lua 脚本，判断库存和一人一单资格
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString()
        );
        if (result == null) {
            return Result.fail("下单失败，请稍后重试");
        }
        if (!result.equals(0L)) {
            int r = result.intValue();
            return Result.fail(r == 2 ? "不能重复下单" : "库存不足");
        }

        // 2. 资格校验通过，发送消息到 RabbitMQ
        SeckillOrderMessage message = new SeckillOrderMessage(orderId, userId, voucherId);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SECKILL_ORDER_EXCHANGE,
                RabbitMqConfig.SECKILL_ORDER_ROUTING_KEY,
                message
        );
        // 3. 立即返回，库存扣减和订单创建由消费者异步处理
        return Result.ok(orderId);
    }

    /**
     * 创建订单：
     * 先加用户级别分布式锁，防止同一用户并发重复处理
     *
     * @param voucherOrder 订单信息
     */
    @Transactional
    @Override
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        RLock lock = redissonClient.getLock(RedisConstants.LOCK_ORDER_KEY + userId);
        boolean isLock = lock.tryLock();
        if (!isLock) {
            log.error("用户{}重复提交订单", userId);
            return;
        }
        try {
            // 通过代理对象调用事务方法，确保事务生效
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            proxy.doCreateVoucherOrder(voucherOrder);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 真正执行库存扣减和订单入库
     *
     * @param voucherOrder 订单信息
     */
    @Transactional
    @Override
    public void doCreateVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();

        // 1. 判断当前用户是否已经购买过当前优惠券
        long count = this.count(new LambdaQueryWrapper<VoucherOrder>()
                .eq(VoucherOrder::getUserId, userId)
                .eq(VoucherOrder::getVoucherId, voucherId));
        if (count > 0) {
            log.error("用户{}已经购买过优惠券{}", userId, voucherId);
            return;
        }

        // 2. 扣减数据库库存
        boolean success = seckillVoucherService.update(new LambdaUpdateWrapper<SeckillVoucher>()
                .eq(SeckillVoucher::getVoucherId, voucherId)
                .gt(SeckillVoucher::getStock, 0)
                .setSql("stock = stock - 1"));
        if (!success) {
            log.error("优惠券{}扣减库存失败", voucherId);
            return;
        }

        // 3. 保存订单到数据库
        success = this.save(voucherOrder);
        if (!success) {
            throw new RuntimeException("创建秒杀订单失败");
        }
    }
}
