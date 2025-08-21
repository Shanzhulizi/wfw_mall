//package com.lm.order.config;
//
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.lm.order.service.MessageLogService;
//
//// 仅负责：消息转换器、RabbitTemplate自定义配置（依赖Spring原生组件）
//@Configuration
//public class RabbitMQTemplateConfig {
//
//    // JSON序列化配置（供RabbitTemplate使用）
//    @Bean
//    public MessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    // 自定义RabbitTemplate（如需确认回调等功能）
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
//                                         MessageConverter messageConverter,
//                                         MessageLogService messageLogService) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        // 设置消息转换器
//        rabbitTemplate.setMessageConverter(messageConverter);
//
//        // 开启消息发送确认（确认消息是否到达交换机）
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                // 消息成功到达交换机，更新消息状态为“已发送”
//                if (correlationData != null && correlationData.getId() != null) {
//                    Long messageLogId = Long.valueOf(correlationData.getId());
//                    messageLogService.updateStatus(messageLogId, 1);
//                }
//            } else {
//                // 消息未到达交换机，记录失败原因
//                System.err.println("消息未到达交换机，原因：" + cause);
//            }
//        });
//
//        // 开启路由失败回调
//        rabbitTemplate.setReturnsCallback(returned -> {
//            System.err.println("消息路由失败：" + returned.getMessage() + "，原因：" + returned.getReplyText());
//        });
//
//        return rabbitTemplate;
//    }
//}
//
