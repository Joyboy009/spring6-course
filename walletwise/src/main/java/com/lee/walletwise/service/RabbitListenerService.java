package com.lee.walletwise.service;

import com.lee.walletwise.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitListenerService {

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_QUEUE)
    public void handleTransactionMessage(String message) {
        log.info("Received RabbitMQ message: {}", message);
        // Could trigger notification logic here
    }
}