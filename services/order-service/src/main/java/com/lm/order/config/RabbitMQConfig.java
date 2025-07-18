package com.lm.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.lm.common.constant.MQConstant.*;


@Configuration
public class RabbitMQConfig {

    // 订单创建交换机
    @Bean
    public Exchange orderCreateExchange() {
        return ExchangeBuilder.directExchange(ORDER_CREATE_EXCHANGE).durable(true).build();
    }

    // 订单创建队列
    @Bean
    public Queue orderCreateQueue() {
        return QueueBuilder.durable(ORDER_CREATE_QUEUE).build();
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

    // JSON 序列化配置（必不可少）
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
