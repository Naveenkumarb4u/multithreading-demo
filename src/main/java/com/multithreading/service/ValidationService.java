package com.multithreading.service;

import com.multithreading.model.MyPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Log4j2
@Component
public class ValidationService {

    public Message<?> validate(Message<?> message) {
        MyPayload payload = (MyPayload) message.getPayload();
        log.info("Thread: " + Thread.currentThread().getName() + " | Stage: Validate | Payload ID: " + payload.getPayloadId());
        return message;
    }
}
