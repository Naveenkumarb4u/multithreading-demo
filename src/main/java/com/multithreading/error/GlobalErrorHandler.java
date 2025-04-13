package com.multithreading.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class GlobalErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandler.class);


    public void handleErrorMessage(Message<?> errorMessage) {
        logger.error("Error processing message: {}", errorMessage);
        // Optionally, implement retry logic, send to a dead-letter queue, etc.
    }
}