package com.hmdp.mq;

import com.hmdp.config.RabbitMqConfig;
import com.hmdp.model.dto.SeckillOrderMessage;
import com.hmdp.model.entity.VoucherOrder;
import com.hmdp.service.IVoucherOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单消费者：
 * 负责监听 RabbitMQ 中的秒杀订单消息，并异步创建订单
 */
@Slf4j
@Component
public class SeckillOrderConsumer {

    @Resource
    private IVoucherOrderService voucherOrderService;

    /**
     * 监听秒杀订单队列
     *
     * @param message 秒杀订单消息
     */
    @RabbitListener(queues = RabbitMqConfig.SECKILL_ORDER_QUEUE)
    public void consume(SeckillOrderMessage message) {
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(message.getOrderId());
        voucherOrder.setUserId(message.getUserId());
        voucherOrder.setVoucherId(message.getVoucherId());
        voucherOrderService.createVoucherOrder(voucherOrder);
    }
}
