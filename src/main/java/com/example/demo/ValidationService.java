package com.example.demo;

import com.example.demo.model.MyPayload;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ValidationService {

    public Message<?> validate(Message<?> message) {
        MyPayload payload = (MyPayload) message.getPayload();
        System.out.println("Thread: " + Thread.currentThread().getName() + " | Stage: Validate | Payload ID: " + payload.getPayloadId());
        return message;
    }
}
