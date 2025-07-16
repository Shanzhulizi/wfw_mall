package com.lm.stock.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.lm.common.constant.MQConstant.*;

@Configuration
public class StockMQConfig {


    @Bean
    public TopicExchange stockEventExchange() {
        return new TopicExchange(STOCK_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue stockDeductQueue() {
        return new Queue(STOCK_DEDUCT_QUEUE, true);
    }

    @Bean
    public Binding stockDeductBinding() {
        return BindingBuilder
                .bind(stockDeductQueue())
                .to(stockEventExchange())
                .with(STOCK_DEDUCT_ROUTING_KEY);
    }
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}