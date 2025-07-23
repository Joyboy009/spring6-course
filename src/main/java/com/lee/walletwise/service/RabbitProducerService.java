package com.lee.walletwise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitProducerService {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${rabbitmq.exchange.transaction}")
    private String transactionExchange;
    
    @Value("${rabbitmq.routing-key.transaction}")
    private String transactionRoutingKey;
    
    public void sendTransactionMessage(String message) {
        try {
            rabbitTemplate.convertAndSend(transactionExchange, transactionRoutingKey, message);
            log.info("Transaction message sent to RabbitMQ: {}", message);
        } catch (Exception e) {
            log.error("Failed to send transaction message to RabbitMQ: {}", e.getMessage());
        }
    }
    
    @RabbitListener(queues = "${rabbitmq.queue.transaction}")
    public void receiveTransactionMessage(String message) {
        log.info("Received transaction message from RabbitMQ: {}", message);
        // Here you can implement notification logic
        // For example: send email, push notification, SMS, etc.
        processTransactionNotification(message);
    }
    
    private void processTransactionNotification(String message) {
        // Simulate notification processing
        log.info("Processing transaction notification: {}", message);
        
        // In a real application, you would implement:
        // 1. Parse the message
        // 2. Extract user information
        // 3. Send appropriate notifications (email, SMS, push notification)
        // 4. Log the notification for audit purposes
        
        // For now, we'll just log the message
        System.out.println("🔔 NOTIFICATION: " + message);
    }
}