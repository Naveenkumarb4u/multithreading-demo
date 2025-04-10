package com.example.demo;

import com.example.demo.model.MyPayload;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Component
public class ThreadAssignmentProcessor implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        // Extract the payloadId and payloadId from the message
        MyPayload payload = (MyPayload) message.getPayload();
        int payloadId = payload.getPayloadId();
         // Assuming the payloadId is part of the payload

        // Get the thread for this payloadId
        Thread thread = ThreadFactoryUtil.getThreadForPayloadId(payloadId);

        // Get a Lock for this payloadId (to ensure sequential processing of same payloadId)
        Lock lock = ThreadFactoryUtil.getLockForPayloadId(payloadId);

        // Acquire the lock for this payloadId to ensure only one thread processes messages with the same payloadId
        lock.lock();
        try {
            // Log the thread name and payloadId
            System.out.println("Thread: " + thread.getName() + " | Payload ID: " + payloadId);

            // Simulate further processing here, which will only happen after the lock is acquired
            Runnable task = () -> {
                System.out.println("Processing message with Payload ID: " + payloadId + " on thread: " + thread.getName());
            };

            // Execute the task using the assigned thread
            thread.start();

        } finally {
            // Release the lock after the processing is done
            lock.unlock();
        }
    }
}