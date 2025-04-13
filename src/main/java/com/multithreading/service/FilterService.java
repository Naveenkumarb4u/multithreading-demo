package com.multithreading.service;

import com.multithreading.model.MyPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class FilterService {
    public boolean filter(Message<?> message) {
        MyPayload payload = (MyPayload) message.getPayload();
        log.info("Thread: " + Thread.currentThread().getName() + " | Stage: Filter | Payload ID: " + payload.getPayloadId());
        return true;
    }
}
