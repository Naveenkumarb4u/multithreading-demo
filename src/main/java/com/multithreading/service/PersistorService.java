package com.multithreading.service;

import com.multithreading.model.MyPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Log4j2
@Component
public class PersistorService {


    @ServiceActivator(inputChannel = "persistChannel")
    public void persist(Message<?> message) {
        Object payloadId = getPayloadId((MyPayload) message.getPayload());
        Lock lock = (Lock) message.getHeaders().get("payloadLock");
        try {
            log.info("Persisting message with payloadId: {} on thread: {}", payloadId, Thread.currentThread().getName());
            // Add persistence logic here
            if (payloadId != null) {
                MyPayload data = (MyPayload) message.getPayload();
                log.info("Successfully persisted: {}", data);
            }
        } finally {
            if (lock != null) {
                lock.unlock();
                log.info("Released lock for payloadId: {}", payloadId);
            }
        }
    }

    private String getPayloadId(MyPayload payload) {
        try {
            if (payload != null) {
                return payload.getPayloadId();
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting payloadId: {}", e.getMessage(), e);
            return null; // Or handle differently
        }
    }
}