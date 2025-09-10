//package com.lm.order.config;
//
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RocketMQConfig {
//
//    @Value("${rocketmq.name-server}")
//    private String nameServer;
//
//    @Value("${rocketmq.producer.group}")
//    private String producerGroup;
//
//    @Bean
//    public RocketMQTemplate rocketMQTemplate() {
//        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
//        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
//        producer.setNamesrvAddr(nameServer);
//        producer.setVipChannelEnabled(false); // 重要：如果是在本地测试，设置为false
//        try {
//            producer.start();
//        } catch (MQClientException e) {
//            throw new RuntimeException("Failed to start RocketMQ producer", e);
//        }
//        rocketMQTemplate.setProducer(producer);
//        return rocketMQTemplate;
//    }
//}