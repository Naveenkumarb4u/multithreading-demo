package com.multithreading.service;

import com.multithreading.model.MyPayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
@Log4j2
@Component
public class ProcessorService {
    public void process(Message<?> message) throws InterruptedException {
        MyPayload payload = (MyPayload) message.getPayload();
        log.info("Thread: " + Thread.currentThread().getName() + " | Stage: Processor | Payload ID: " + payload.getPayloadId());
        //System.out.println(Thread.currentThread().getName()+" is waiting...");
        Thread.sleep(5000);
        log.info(Thread.currentThread().getName()+" is released...");
    }
}
