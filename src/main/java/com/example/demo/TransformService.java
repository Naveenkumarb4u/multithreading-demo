package com.example.demo;

import com.example.demo.model.MyPayload;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class TransformService {
    public Object transform(Message<?> message) {
        MyPayload payload = (MyPayload) message.getPayload();
        System.out.println("Thread: " + Thread.currentThread().getName() + " | Stage: Transform | Payload ID: " + payload.getPayloadId());
        return payload;
    }
}
