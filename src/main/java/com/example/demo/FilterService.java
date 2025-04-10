package com.example.demo;

import com.example.demo.model.MyPayload;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class FilterService {
    public boolean filter(Message<?> message) {
        MyPayload payload = (MyPayload) message.getPayload();
        System.out.println("Thread: " + Thread.currentThread().getName() + " | Stage: Filter | Payload ID: " + payload.getPayloadId());
        return true;
    }
}
