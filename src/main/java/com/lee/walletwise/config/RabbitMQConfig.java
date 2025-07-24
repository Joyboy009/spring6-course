package com.lee.walletwise.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    
    @Value("${rabbitmq.queue.transaction}")
    private String transactionQueue;
    
    @Value("${rabbitmq.exchange.transaction}")
    private String transactionExchange;
    
    @Value("${rabbitmq.routing-key.transaction}")
    private String transactionRoutingKey;
    
    @Bean
    public Queue transactionQueue() {
        return new Queue(transactionQueue, true);
    }
    
    @Bean
    public DirectExchange transactionExchange() {
        return new DirectExchange(transactionExchange);
    }
    
    @Bean
    public Binding transactionBinding() {
        return BindingBuilder
                .bind(transactionQueue())
                .to(transactionExchange())
                .with(transactionRoutingKey);
    }
    
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}