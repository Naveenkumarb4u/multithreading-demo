package com.example.demo;

import com.example.demo.model.MyPayload;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ProcessorService {
    public void process(Message<?> message) throws InterruptedException {
        MyPayload payload = (MyPayload) message.getPayload();
        System.out.println("Thread: " + Thread.currentThread().getName() + " | Stage: Processor | Payload ID: " + payload.getPayloadId());
        //System.out.println(Thread.currentThread().getName()+" is waiting...");
        //Thread.sleep(5000);
        System.out.println(Thread.currentThread().getName()+" is released...");
    }
}
