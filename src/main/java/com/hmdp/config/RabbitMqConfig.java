package com.hmdp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置：
 * 定义秒杀订单使用的交换机、队列和绑定关系
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 秒杀订单交换机
     */
    public static final String SECKILL_ORDER_EXCHANGE = "seckill.order.exchange";

    /**
     * 秒杀订单队列
     */
    public static final String SECKILL_ORDER_QUEUE = "seckill.order.queue";

    /**
     * 秒杀订单路由键
     */
    public static final String SECKILL_ORDER_ROUTING_KEY = "seckill.order";

    /**
     * 创建直连交换机
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange seckillOrderExchange() {
        return new DirectExchange(SECKILL_ORDER_EXCHANGE, true, false);
    }

    /**
     * 创建持久化队列
     *
     * @return Queue
     */
    @Bean
    public Queue seckillOrderQueue() {
        return QueueBuilder.durable(SECKILL_ORDER_QUEUE).build();
    }

    /**
     * 绑定队列和交换机
     *
     * @param seckillOrderQueue 队列
     * @param seckillOrderExchange 交换机
     * @return Binding
     */
    @Bean
    public Binding seckillOrderBinding(Queue seckillOrderQueue, DirectExchange seckillOrderExchange) {
        return BindingBuilder.bind(seckillOrderQueue)
                .to(seckillOrderExchange)
                .with(SECKILL_ORDER_ROUTING_KEY);
    }
}
