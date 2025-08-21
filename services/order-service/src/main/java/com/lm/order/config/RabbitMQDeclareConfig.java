package com.lm.order.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 仅负责：声明交换机、队列、绑定关系（不依赖RabbitTemplate）
@Configuration
public class RabbitMQDeclareConfig {

    // 常量定义（建议放在单独的常量类中，这里简化处理）
    public static final String ORDER_CREATE_EXCHANGE = "order.create.exchange";
    public static final String ORDER_CREATE_QUEUE = "order.create.queue";
    public static final String ORDER_CREATE_ROUTING_KEY = "order.create.key";

    // 订单创建交换机
    @Bean
    public Exchange orderCreateExchange() {
        return ExchangeBuilder.directExchange(ORDER_CREATE_EXCHANGE)
                .durable(true) // 持久化
                .build();
    }

    // 订单创建队列
    @Bean
    public Queue orderCreateQueue() {
        return QueueBuilder.durable(ORDER_CREATE_QUEUE)
                .build();
    }

    // 队列绑定交换机
    @Bean
    public Binding orderCreateBinding() {
        return BindingBuilder
                .bind(orderCreateQueue())
                .to(orderCreateExchange())
                .with(ORDER_CREATE_ROUTING_KEY)
                .noargs();
    }

    // 如需其他队列/交换机，继续在此类中添加...
}
