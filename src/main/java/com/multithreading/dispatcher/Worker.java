package com.multithreading.dispatcher;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;

@Log4j2
class Worker implements Runnable {
    private final String threadName;
    private final BlockingQueue<Runnable> taskQueue;
    private final PayloadProcessor processor;

    Worker(String threadName, BlockingQueue<Runnable> taskQueue, PayloadProcessor processor) {
        this.threadName = threadName;
        this.taskQueue = taskQueue;
        this.processor = processor;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(threadName);
        log.info("Worker {} started.", threadName);
        try {
            while (true) {
                Runnable task = taskQueue.take();
                task.run(); // Execute the Runnable which will call processPayload
            }
        } catch (InterruptedException e) {
            log.warn("Worker {} interrupted.", threadName);
            Thread.currentThread().interrupt();
        } finally {
            log.info("Worker {} finished.", threadName);
        }
    }
}

interface PayloadProcessor {
    void process(Message<?> message, Lock lock, Object payloadId);
}