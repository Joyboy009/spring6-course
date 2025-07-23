package com.lee.walletwise.service;

import com.lee.walletwise.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitProducerService {

    private final RabbitTemplate rabbitTemplate;

    public void publishTransactionSuccess(Long userId) {
        String message = "Transaction successful for user " + userId;
        rabbitTemplate.convertAndSend(RabbitMQConfig.TRANSACTION_EXCHANGE, RabbitMQConfig.TRANSACTION_ROUTING_KEY, message);
    }
}