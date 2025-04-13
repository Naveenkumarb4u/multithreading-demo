package com.multithreading.dispatcher;

import com.multithreading.model.MyPayload;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Component
public class PayloadIdBasedDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(PayloadIdBasedDispatcher.class);
    private final Map<Object, Lock> payloadLocks = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor taskExecutor;
    private final MessageChannel validatedChannel;
    private final Map<String, BlockingQueue<Runnable>> threadQueues = new ConcurrentHashMap<>();
    private final Map<Object, String> payloadThreadAssignment = new ConcurrentHashMap<>(); // Map payloadId to thread name


    @Autowired
    public PayloadIdBasedDispatcher(@Qualifier("payloadTaskExecutor") ThreadPoolExecutor taskExecutor,
                                    @Qualifier("executorChannel") MessageChannel validatedChannel) {
        this.taskExecutor = taskExecutor;
        this.validatedChannel = validatedChannel;

        for (int i = 0; i < this.taskExecutor.getCorePoolSize(); i++) {
            String threadName = "thread-" + (i + 1);
            threadQueues.put(threadName, new LinkedBlockingQueue<>());
            this.taskExecutor.execute(new Worker(threadName, threadQueues.get(threadName), this::processPayload));
        }
    }

    /*    @ServiceActivator(inputChannel = "inputChannel")
        public void dispatchMessage(Message<?> message) {
            String payloadId = getPayloadId((MyPayload) message.getPayload());
            logger.info("dispatchMessage: Received message with payloadId: {}", payloadId);
            if (payloadId != null) {
                Lock lock = payloadLocks.computeIfAbsent(payloadId, k -> new ReentrantLock());
                logger.info("dispatchMessage: Got lock for payloadId: {}", payloadId);
                lock.lock();
                try {
                    int hashCode = payloadId.hashCode();
                    int threadIndex = Math.abs(hashCode % taskExecutor.getCorePoolSize());
                    String targetThreadName = "thread-" + (threadIndex + 1);
                    logger.info("dispatchMessage: Target thread for payloadId {}: {}", payloadId, targetThreadName);

                    BlockingQueue<Runnable> targetQueue = threadQueues.get(targetThreadName);
                    if (targetQueue != null) {
                        logger.info("Offering to queue {} for thread {}", System.identityHashCode(targetQueue), targetThreadName);
                        *//*boolean offered = targetQueue.offer(() -> {
                        logger.info("Worker processing payloadId: {} on thread: {}", payloadId, Thread.currentThread().getName());
                        validatedChannel.send(MessageBuilder.fromMessage(message)
                                .setHeader("payloadLock", lock)
                                .setHeader("payloadId", payloadId)
                                .build());
                    });*//*
                    if (targetQueue != null) {
                        targetQueue.offer(() -> processPayload(message, lock, payloadId));
                    } else {
                        logger.warn("No queue found for target thread: {}", targetThreadName);
                        taskExecutor.execute(() -> processPayload(message, lock, payloadId)); // Fallback
                    }

                  *//*  if (offered) {
                        logger.info("dispatchMessage: Offered task for payloadId {} to queue {}: {}", payloadId, targetThreadName, offered);
                    } else {
                        logger.warn("dispatchMessage: Failed to offer task for payloadId {} to queue {}", payloadId, targetThreadName);
                        // Consider alternative handling if offer fails
                    }*//*
                } else {
                    logger.warn("dispatchMessage: No queue found for target thread: {}", targetThreadName);
                    // Fallback
                    taskExecutor.execute(() -> {
                        logger.warn("dispatchMessage: Fallback processing for payloadId: {} on thread: {}", payloadId, Thread.currentThread().getName());
                        validatedChannel.send(MessageBuilder.fromMessage(message)
                                .setHeader("payloadLock", lock)
                                .setHeader("payloadId", payloadId)
                                .build());
                    });
                }

            } finally {
                // Do not unlock here. Downstream will handle it.
            }
        } else {
            logger.warn("dispatchMessage: Received message without payloadId: {}", message.getPayload());
            taskExecutor.execute(() -> validatedChannel.send(message));
        }
    }*/
    @ServiceActivator(inputChannel = "inputChannel")
    public void dispatchMessage(Message<?> message) {
        Object payloadId = getPayloadId((MyPayload) message.getPayload());

        if (payloadId != null) {
            Lock lock = payloadLocks.computeIfAbsent(payloadId, k -> new ReentrantLock());
            String targetThreadName = getTargetThreadName(payloadId);
            BlockingQueue<Runnable> targetQueue = threadQueues.get(targetThreadName);

            if (targetQueue != null) {
                targetQueue.offer(() -> processPayload(message, lock, payloadId));
            } else {
                logger.warn("No queue found for target thread: {}", targetThreadName);
                taskExecutor.execute(() -> processPayload(message, lock, payloadId)); // Fallback
            }
        } else {
            logger.warn("Received message without payloadId: {}", message.getPayload());
            taskExecutor.execute(() -> validatedChannel.send(message));
        }
    }

    private String getTargetThreadName(Object payloadId) {
        int hashCode = payloadId.hashCode();
        int threadIndex = Math.abs(hashCode % taskExecutor.getCorePoolSize());
        return "thread-" + (threadIndex + 1);
    }

    private void processPayload(Message<?> message, Lock lock, Object payloadId) {
        lock.lock();
        try {
            logger.info("Processing payloadId: {} on thread: {}", payloadId, Thread.currentThread().getName());
            validatedChannel.send(MessageBuilder.fromMessage(message)
                    .setHeader("payloadLock", lock)
                    .setHeader("payloadId", payloadId)
                    .build());
        } finally {
            lock.unlock();
            logger.info("Released lock for payloadId: {} on thread: {}", payloadId, Thread.currentThread().getName());
        }
    }

    private String getPayloadId(MyPayload payload) {
        try {
            if (payload != null) {
                return payload.getPayloadId();
            }
            return null;
        } catch (Exception e) {
            logger.error("Error getting payloadId: {}", e.getMessage(), e);
            return null; // Or handle differently
        }
    }

   /* static class Worker implements Runnable {
            private final String threadName;
            private final BlockingQueue<Runnable> taskQueue;

            Worker(String threadName, BlockingQueue<Runnable> taskQueue) {
                this.threadName = threadName;
                this.taskQueue = taskQueue;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(threadName);
                try {
                    while (true) {
                        Runnable task = taskQueue.take();
                        try {
                            logger.info("Worker {} taking from queue {}", threadName, System.identityHashCode(taskQueue));

                            task.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("Exception during task execution in thread {}: {}", threadName, e.getMessage(), e);
                            // Optionally, handle the error (e.g., retry, send to error queue)
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }*/

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down PayloadIdBasedDispatcher...");
        taskExecutor.shutdown();
        try {
            if (!taskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("PayloadTaskExecutor did not terminate in time, forcing shutdown.");
                taskExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            taskExecutor.shutdownNow();
        }
        logger.info("PayloadIdBasedDispatcher shutdown complete.");
    }
}